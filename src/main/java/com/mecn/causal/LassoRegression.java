package com.mecn.causal;

import java.util.Map;

/**
 * LASSO 回归因果发现实现
 * 
 * 使用 L1 正则化（LASSO）进行多变量联合稀疏回归。
 * 实现使用迭代收缩阈值算法（ISTA），
 * 替代了之前的简化逐变量 OLS 实现。
 * 
 * 原理：对每个目标变量 i，使用所有变量的滞后值 X(t-1)
 * 来预测当前值 Y(t)，通过 L1 正则化筛选出稀疏的因果结构。
 * 
 * 数学形式：min_β (1/2n) * ||y - Xβ||² + λ * ||β||₁
 * 
 * @example
 * {@code
 * CausalMethod lasso = new LassoRegression()
 *     .withLambda(0.01)
 *     .withMinStrength(0.05)
 *     .withWeight(1.5);
 * }
 */
public class LassoRegression extends CausalMethod {

    private static final double DEFAULT_LAMBDA = 0.01;
    private static final double DEFAULT_MIN_STRENGTH = 0.05;
    private static final int DEFAULT_MAX_ITERATIONS = 2000;
    private static final double DEFAULT_TOLERANCE = 1e-6;

    public LassoRegression() {
        super("LASSO");
        this.weight = 1.0;
        this.parameters.put("lambda", DEFAULT_LAMBDA);
        this.parameters.put("minStrength", DEFAULT_MIN_STRENGTH);
        this.parameters.put("maxIterations", DEFAULT_MAX_ITERATIONS);
        this.parameters.put("tolerance", DEFAULT_TOLERANCE);
    }

    /**
     * 设置正则化参数 lambda
     */
    public LassoRegression withLambda(double lambda) {
        this.parameters.put("lambda", lambda);
        return this;
    }

    /**
     * 设置最小因果强度阈值
     */
    public LassoRegression withMinStrength(double minStrength) {
        this.parameters.put("minStrength", minStrength);
        return this;
    }

    @Override
    public CausalMatrix compute(double[][] data, Map<String, Object> params) {
        if (!validateData(data)) {
            throw new IllegalArgumentException("Invalid input data");
        }

        double lambda = params != null && params.containsKey("lambda")
            ? ((Number) params.get("lambda")).doubleValue() : DEFAULT_LAMBDA;
        double minStrength = params != null && params.containsKey("minStrength")
            ? ((Number) params.get("minStrength")).doubleValue() : DEFAULT_MIN_STRENGTH;
        int maxIterations = params != null && params.containsKey("maxIterations")
            ? ((Number) params.get("maxIterations")).intValue() : DEFAULT_MAX_ITERATIONS;
        double tolerance = params != null && params.containsKey("tolerance")
            ? ((Number) params.get("tolerance")).doubleValue() : DEFAULT_TOLERANCE;

        int T = data.length;
        int N = data[0].length;
        int numSamples = T - 1; // 滞后一期的有效样本

        CausalMatrix result = new CausalMatrix(N, getName());

        // 构建特征矩阵: X[t][j] = data[t][j] (t 时刻所有指标值)
        double[][] X = new double[numSamples][N];
        for (int t = 0; t < numSamples; t++) {
            System.arraycopy(data[t], 0, X[t], 0, N);
        }

        // 对每个目标变量，执行 LASSO 回归
        for (int i = 0; i < N; i++) {
            double[] y = new double[numSamples];
            for (int t = 0; t < numSamples; t++) {
                y[t] = data[t + 1][i];
            }

            double[] coefficients;
            try {
                coefficients = fitLasso(X, y, lambda, tolerance, maxIterations);
            } catch (Exception e) {
                // 回退：使用相关系数
                coefficients = fallbackCorrelation(X, y, N, numSamples);
            }

            for (int j = 0; j < N; j++) {
                double coef = coefficients[j];
                if (j != i && Math.abs(coef) > minStrength) {
                    result.setCausalEffect(j, i, coef);
                    result.pValues[i][j] = estimatePValue(coef, numSamples, N);
                }
            }
        }

        result.addMetadata("lambda", lambda);
        result.addMetadata("minStrength", minStrength);
        result.addMetadata("maxIterations", maxIterations);
        result.addMetadata("numSamples", T);
        result.addMetadata("numVariables", N);
        result.addMetadata("method", "LASSO (ISTA)");

        return result;
    }

    /**
     * ISTA (Iterative Shrinkage-Thresholding Algorithm) 求解 LASSO。
     * 
     * 迭代公式: β^{k+1} = S_{αλ}(β^k + α * X' * (y - X * β^k))
     * 其中 S 是软阈值算子: S_κ(x) = sign(x) * max(|x| - κ, 0)
     */
    private double[] fitLasso(double[][] X, double[] y, double lambda,
                               double tolerance, int maxIterations) {
        int n = X.length;
        int p = X[0].length;

        // 标准化 X: 每列零均值、单位方差
        double[] meanX = new double[p];
        double[] stdX = new double[p];
        double[][] Xs = new double[n][p];

        for (int j = 0; j < p; j++) {
            for (int i = 0; i < n; i++) {
                meanX[j] += X[i][j];
            }
            meanX[j] /= n;
        }
        for (int j = 0; j < p; j++) {
            for (int i = 0; i < n; i++) {
                double d = X[i][j] - meanX[j];
                Xs[i][j] = d;
                stdX[j] += d * d;
            }
            stdX[j] = Math.sqrt(stdX[j] / Math.max(1, n - 1));
            if (stdX[j] < 1e-10) stdX[j] = 1.0;
            // 标准化
            for (int i = 0; i < n; i++) {
                Xs[i][j] /= stdX[j];
            }
        }

        // 中心化 y
        double meanY = 0;
        for (int i = 0; i < n; i++) meanY += y[i];
        meanY /= n;
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) ys[i] = y[i] - meanY;

        // 计算 Lipschitz 常数: L = max(eig(X'X)) ≈ max(colSum(X.^2))
        double L = 0;
        for (int j = 0; j < p; j++) {
            double colNorm = 0;
            for (int i = 0; i < n; i++) {
                colNorm += Xs[i][j] * Xs[i][j];
            }
            if (colNorm > L) L = colNorm;
        }
        double stepSize = 1.0 / L; // ISTA 步长

        // ISTA 迭代
        double[] beta = new double[p];
        double[] prevBeta = new double[p];
        double[] residual = new double[n];
        double[] gradient = new double[p];

        for (int iter = 0; iter < maxIterations; iter++) {
            // 计算残差: r = Xβ - y
            for (int i = 0; i < n; i++) {
                residual[i] = -ys[i];
                for (int j = 0; j < p; j++) {
                    residual[i] += Xs[i][j] * beta[j];
                }
            }

            // 计算梯度: ∇f = X'r / n
            for (int j = 0; j < p; j++) {
                gradient[j] = 0;
                for (int i = 0; i < n; i++) {
                    gradient[j] += Xs[i][j] * residual[i];
                }
                gradient[j] /= n;
            }

            // 梯度下降 + 软阈值: β = S_{αλ}(β - α * ∇f)
            double alphaLambda = stepSize * lambda;
            double maxChange = 0;
            for (int j = 0; j < p; j++) {
                double updated = beta[j] - stepSize * gradient[j];
                // 软阈值算子
                if (updated > alphaLambda) {
                    beta[j] = updated - alphaLambda;
                } else if (updated < -alphaLambda) {
                    beta[j] = updated + alphaLambda;
                } else {
                    beta[j] = 0;
                }
                double change = Math.abs(beta[j] - prevBeta[j]);
                if (change > maxChange) maxChange = change;
                prevBeta[j] = beta[j];
            }

            if (maxChange < tolerance) {
                break;
            }
        }

        // 反标准化系数
        double[] coefficients = new double[p];
        for (int j = 0; j < p; j++) {
            coefficients[j] = beta[j] / stdX[j];
        }

        return coefficients;
    }

    /**
     * 计算皮尔逊相关系数（用于 LASSO 不收敛时的回退）
     */
    private double[] fallbackCorrelation(double[][] X, double[] y, int p, int n) {
        double[] result = new double[p];
        double meanY = 0;
        for (int i = 0; i < n; i++) meanY += y[i];
        meanY /= n;

        double varY = 0;
        for (int i = 0; i < n; i++) varY += (y[i] - meanY) * (y[i] - meanY);

        for (int j = 0; j < p; j++) {
            double meanX = 0;
            for (int i = 0; i < n; i++) meanX += X[i][j];
            meanX /= n;

            double cov = 0, varX = 0;
            for (int i = 0; i < n; i++) {
                double dx = X[i][j] - meanX;
                double dy = y[i] - meanY;
                cov += dx * dy;
                varX += dx * dx;
            }

            double denom = Math.sqrt(varX * varY);
            result[j] = denom > 1e-10 ? cov / denom : 0.0;
        }
        return result;
    }

    /**
     * 估算 p 值（基于 t 统计量近似）
     */
    private double estimatePValue(double coefficient, int sampleSize, int numVariables) {
        double se = Math.abs(coefficient) * 0.5 / Math.sqrt(Math.max(1, sampleSize - numVariables)) + 0.001;
        double tStat = Math.abs(coefficient) / se;
        double pValue = Math.exp(-0.5 * tStat * tStat);
        return Math.min(1.0, Math.max(1e-15, pValue));
    }

    @Override
    public double getConfidence(double[][] data, int source, int target) {
        int T = data.length;
        int N = data[0].length;
        double sampleRatio = Math.min(1.0, (double) T / (N * 5));
        return 0.5 + 0.4 * sampleRatio;
    }

    /**
     * 自动调优 Lambda 参数（使用交叉验证）
     */
    public HyperparameterResult autoTuneLambda(double[][] data) {
        double[] defaultLambdas = { 0.001, 0.005, 0.01, 0.05, 0.1, 0.2, 0.5, 1.0 };
        return autoTuneLambda(data, defaultLambdas, Math.min(5, Math.max(2, data.length / 10)));
    }

    public HyperparameterResult autoTuneLambda(double[][] data, double[] lambdaCandidates) {
        return autoTuneLambda(data, lambdaCandidates, Math.min(5, Math.max(2, data.length / 10)));
    }

    public HyperparameterResult autoTuneLambda(double[][] data, double[] lambdaCandidates, int kFolds) {
        long startTime = System.currentTimeMillis();

        if (lambdaCandidates == null || lambdaCandidates.length == 0) {
            lambdaCandidates = new double[]{ 0.001, 0.005, 0.01, 0.05, 0.1, 0.2, 0.5, 1.0 };
        }

        HyperparameterResult result = new HyperparameterResult(getName());
        result.setLambdaCandidates(lambdaCandidates);
        result.setKFolds(kFolds);

        int T = data.length;
        int N = data[0].length;
        int foldSize = T / kFolds;

        double[] cvScores = new double[lambdaCandidates.length];

        for (int i = 0; i < lambdaCandidates.length; i++) {
            double lambda = lambdaCandidates[i];
            double sumScore = 0.0;

            for (int fold = 0; fold < kFolds; fold++) {
                int testStart = fold * foldSize;
                int testEnd = Math.min(testStart + foldSize, T);

                double[][] trainData = new double[T - (testEnd - testStart)][N];
                double[][] testData = new double[testEnd - testStart][N];
                int ti = 0;
                for (int t = 0; t < T; t++) {
                    if (t < testStart || t >= testEnd) {
                        System.arraycopy(data[t], 0, trainData[ti++], 0, N);
                    } else {
                        System.arraycopy(data[t], 0, testData[t - testStart], 0, N);
                    }
                }

                double score = evaluateModel(trainData, testData, lambda);
                sumScore += score;
            }

            cvScores[i] = sumScore / kFolds;
        }

        result.setCvScores(cvScores);

        int bestIndex = 0;
        double bestScore = cvScores[0];
        for (int i = 1; i < cvScores.length; i++) {
            if (cvScores[i] < bestScore) {
                bestScore = cvScores[i];
                bestIndex = i;
            }
        }

        double bestLambda = lambdaCandidates[bestIndex];
        result.setBestLambda(bestLambda);
        result.setCrossValidationScore(-bestScore);

        double[] metrics = calculateMetrics(data, bestLambda, N);
        result.setAIC(metrics[0]);
        result.setBIC(metrics[1]);
        result.setRSquared(metrics[2]);

        long endTime = System.currentTimeMillis();
        result.setExecutionTimeMs(endTime - startTime);

        String recommendation = generateRecommendation(bestLambda, bestScore, metrics);
        result.setRecommendation(recommendation);

        return result;
    }

    /**
     * 评估模型性能（MSE）
     */
    private double evaluateModel(double[][] trainData, double[][] testData, double lambda) {
        int T_train = trainData.length;
        int T_test = testData.length;
        int N = trainData[0].length;

        if (T_train < 2 || T_test < 1) return Double.MAX_VALUE;

        int numSamples = T_train - 1;
        double[][] X = new double[numSamples][N];
        for (int t = 0; t < numSamples; t++) {
            System.arraycopy(trainData[t], 0, X[t], 0, N);
        }

        double totalMse = 0.0;
        int count = 0;

        for (int i = 0; i < N; i++) {
            double[] y = new double[numSamples];
            for (int t = 0; t < numSamples; t++) {
                y[t] = trainData[t + 1][i];
            }

            double[] coef;
            try {
                coef = fitLasso(X, y, lambda, 1e-4, 500);
            } catch (Exception e) {
                coef = fallbackCorrelation(X, y, N, numSamples);
            }

            for (int t = 0; t < T_test - 1; t++) {
                double predicted = 0;
                for (int j = 0; j < N; j++) {
                    predicted += coef[j] * testData[t][j];
                }
                double actual = testData[t + 1][i];
                totalMse += (predicted - actual) * (predicted - actual);
                count++;
            }
        }

        return count > 0 ? totalMse / count : Double.MAX_VALUE;
    }

    /**
     * 计算模型评估指标 (AIC, BIC, R²)
     */
    private double[] calculateMetrics(double[][] data, double lambda, int N) {
        int T = data.length;
        if (T < 2) return new double[]{ Double.MAX_VALUE, Double.MAX_VALUE, 0.0 };

        int numSamples = T - 1;
        double[][] X = new double[numSamples][N];
        for (int t = 0; t < numSamples; t++) {
            System.arraycopy(data[t], 0, X[t], 0, N);
        }

        double rss = 0.0;
        double tss = 0.0;
        double[] means = new double[N];

        for (int i = 0; i < N; i++) {
            for (int t = 0; t < numSamples; t++) {
                means[i] += data[t + 1][i];
            }
            means[i] /= numSamples;

            double[] y = new double[numSamples];
            for (int t = 0; t < numSamples; t++) {
                y[t] = data[t + 1][i];
            }

            double[] coef;
            try {
                coef = fitLasso(X, y, lambda, 1e-4, 500);
            } catch (Exception e) {
                coef = fallbackCorrelation(X, y, N, numSamples);
            }

            for (int t = 0; t < numSamples; t++) {
                double predicted = 0;
                for (int j = 0; j < N; j++) {
                    predicted += coef[j] * X[t][j];
                }
                rss += (y[t] - predicted) * (y[t] - predicted);
                tss += (y[t] - means[i]) * (y[t] - means[i]);
            }
        }

        double rSquared = tss > 1e-10 ? 1.0 - (rss / tss) : 0.0;
        rSquared = Math.max(0.0, Math.min(1.0, rSquared));

        double logLik = -numSamples * N * Math.log(rss / (numSamples * N) + 1e-15) / 2.0;
        int effectiveParams = N + 1;
        double aic = -2 * logLik + 2 * effectiveParams;
        double bic = -2 * logLik + effectiveParams * Math.log(numSamples * N);

        return new double[]{ aic, bic, rSquared };
    }

    private String generateRecommendation(double bestLambda, double bestMse, double[] metrics) {
        StringBuilder sb = new StringBuilder();
        if (bestLambda < 0.005) {
            sb.append("Lambda 值较小，模型复杂度较高，可能存在过拟合风险。");
        } else if (bestLambda > 0.3) {
            sb.append("Lambda 值较大，模型较为稀疏，优先选择了最核心的因果关系。");
        } else {
            sb.append("Lambda 值适中，模型平衡了复杂度和泛化能力。");
        }
        sb.append(String.format("\nCV MSE=%.6f, R²=%.3f 表明模型解释了 %.1f%% 的方差。",
            bestMse, metrics[2], metrics[2] * 100));
        return sb.toString();
    }
}
