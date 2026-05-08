package com.mecn.preprocess;

/**
 * ADF (Augmented Dickey-Fuller) 平稳性检验
 * 
 * 用于检验时间序列是否包含单位根（即是否非平稳）
 * 
 * 原假设 H0: 序列存在单位根（非平稳）
 * 备择假设 H1: 序列不存在单位根（平稳）
 * 
 * @example
 * {@code
 * double[] data = {...}; // 时间序列数据
 * ADFTestadfTest = new ADFTest();
 * ADFResult result = adfTest.test(data);
 * 
 * if (result.isStationary(0.05)) {
 *     System.out.println("在 5% 显著性水平下，序列是平稳的");
 * } else {
 *     System.out.println("在 5% 显著性水平下，序列是非平稳的");
 * }
 * }
 */
public class ADFTest {
    
    private static final int DEFAULT_MAX_LAG = 25;  // 默认最大滞后阶数
    
    private int selectedLag;
    private double testStatistic;
    private double pValue;
    private double[] criticalValues;
    
    /**
     * 执行 ADF 检验
     * 
     * @param data 时间序列数据
     * @return ADF 检验结果
     */
    public ADFResult test(double[] data) {
        return test(data, null);
    }
    
    /**
     * 执行 ADF 检验（可指定模型类型）
     * 
     * @param data 时间序列数据
     * @param modelType 模型类型："c" (仅截距), "ct" (截距 + 趋势), "n" (无)
     * @return ADF 检验结果
     */
    public ADFResult test(double[] data, String modelType) {
        if (data == null || data.length < 10) {
            throw new IllegalArgumentException("数据长度至少需要 10 个观测值");
        }
        
        int n = data.length;
        
        // 1. 选择最优滞后阶数（使用 AIC 准则）
        selectedLag = selectOptimalLag(data);
        
        // 2. 构建回归模型并估计参数
        double[] y = new double[n - selectedLag - 1];
        double[][] X = buildDesignMatrix(data, selectedLag, modelType != null ? modelType : "c");
        
        for (int i = 0; i < y.length; i++) {
            y[i] = data[selectedLag + 1 + i] - data[selectedLag + i];  // Δy_t
        }
        
        // 3. 使用 OLS 估计参数
        double[] beta = olsEstimate(X, y);
        
        // 4. 获取检验统计量（滞后项系数的 t 统计量）
        testStatistic = calculateTStatistic(X, y, beta);
        
        // 5. 计算 p 值（使用 MacKinnon 近似）
        pValue = calculatePValue(testStatistic, modelType != null ? modelType : "c");
        
        // 6. 临界值
        criticalValues = getCriticalValues(modelType != null ? modelType : "c");
        
        return new ADFResult(testStatistic, pValue, criticalValues, selectedLag);
    }
    
    /**
     * 选择最优滞后阶数（使用 AIC 准则）
     */
    private int selectOptimalLag(double[] data) {
        int n = data.length;
        int maxLag = Math.min(DEFAULT_MAX_LAG, n / 4);
        
        double minAIC = Double.MAX_VALUE;
        int optimalLag = 0;
        
        for (int lag = 0; lag <= maxLag; lag++) {
            double aic = calculateAIC(data, lag);
            if (aic < minAIC) {
                minAIC = aic;
                optimalLag = lag;
            }
        }
        
        return optimalLag;
    }
    
    /**
     * 计算 AIC
     */
    private double calculateAIC(double[] data, int lag) {
        int n = data.length - lag - 1;
        double[] y = new double[n];
        double[][] X = buildDesignMatrix(data, lag, "c");
        
        for (int i = 0; i < n; i++) {
            y[i] = data[lag + 1 + i] - data[lag + i];
        }
        
        double[] beta = olsEstimate(X, y);
        double rss = calculateRSS(X, y, beta);
        
        int k = X[0].length;  // 参数个数
        return n * Math.log(rss / n) + 2 * k;
    }
    
    /**
     * 构建设计矩阵
     */
    private double[][] buildDesignMatrix(double[] data, int lag, String modelType) {
        int n = data.length - lag - 1;
        int numCols = 1 + lag;  // y_{t-1} + lag 个差分项
        
        // 根据模型类型增加列
        if ("c".equals(modelType) || "ct".equals(modelType)) {
            numCols += 1;  // 截距项
        }
        if ("ct".equals(modelType)) {
            numCols += 1;  // 趋势项
        }
        
        double[][] X = new double[n][numCols];
        
        for (int t = 0; t < n; t++) {
            int colIndex = 0;
            
            // 添加 y_{t-1}
            X[t][colIndex++] = data[lag + t];
            
            // 添加截距项
            if ("c".equals(modelType) || "ct".equals(modelType)) {
                X[t][colIndex++] = 1.0;
            }
            
            // 添加趋势项
            if ("ct".equals(modelType)) {
                X[t][colIndex++] = t + 1;
            }
            
            // 添加滞后差分项
            for (int j = 1; j <= lag; j++) {
                X[t][colIndex++] = data[lag + t - j + 1] - data[lag + t - j];
            }
        }
        
        return X;
    }
    
    /**
     * OLS 参数估计
     */
    private double[] olsEstimate(double[][] X, double[] y) {
        int n = X.length;
        int k = X[0].length;
        
        // 简化实现：使用正规方程 (X'X)^(-1)X'y
        // 这里使用简化的方法，实际应该使用矩阵库
        
        double[] beta = new double[k];
        
        // 对于简化情况，直接使用样本矩
        if (k == 1) {
            // 仅一个解释变量
            double sumXY = 0.0;
            double sumXX = 0.0;
            
            for (int i = 0; i < n; i++) {
                sumXY += X[i][0] * y[i];
                sumXX += X[i][0] * X[i][0];
            }
            
            beta[0] = sumXX > 1e-10 ? sumXY / sumXX : 0.0;
        } else {
            // 多个解释变量：使用迭代方法
            // 这是一个简化实现，实际应该使用矩阵求逆
            
            // 初始化
            for (int j = 0; j < k; j++) {
                beta[j] = 0.0;
            }
            
            // 使用梯度下降法近似（简化）
            double learningRate = 0.0001;
            int maxIter = 1000;
            
            for (int iter = 0; iter < maxIter; iter++) {
                double[] gradient = new double[k];
                
                for (int j = 0; j < k; j++) {
                    for (int i = 0; i < n; i++) {
                        double residual = y[i] - predict(X[i], beta);
                        gradient[j] += -2 * X[i][j] * residual;
                    }
                }
                
                // 更新参数
                for (int j = 0; j < k; j++) {
                    beta[j] -= learningRate * gradient[j] / n;
                }
            }
        }
        
        return beta;
    }
    
    /**
     * 预测值
     */
    private double predict(double[] x, double[] beta) {
        double pred = 0.0;
        for (int j = 0; j < beta.length; j++) {
            pred += x[j] * beta[j];
        }
        return pred;
    }
    
    /**
     * 计算残差平方和
     */
    private double calculateRSS(double[][] X, double[] y, double[] beta) {
        double rss = 0.0;
        for (int i = 0; i < X.length; i++) {
            double residual = y[i] - predict(X[i], beta);
            rss += residual * residual;
        }
        return rss;
    }
    
    /**
     * 计算 t 统计量
     */
    private double calculateTStatistic(double[][] X, double[] y, double[] beta) {
        int n = X.length;
        int k = X[0].length;
        
        double rss = calculateRSS(X, y, beta);
        double sigmaSquared = rss / (n - k);
        
        // 计算第一个系数（y_{t-1} 的系数）的标准误
        // 简化实现：假设设计矩阵正交
        double sumXX = 0.0;
        for (int i = 0; i < n; i++) {
            sumXX += X[i][0] * X[i][0];
        }
        
        double seBeta0 = Math.sqrt(sigmaSquared / sumXX);
        
        return seBeta0 > 1e-10 ? beta[0] / seBeta0 : 0.0;
    }
    
    /**
     * 计算 p 值（MacKinnon 近似）
     */
    private double calculatePValue(double tau, String modelType) {
        // MacKinnon (1996) 近似公式
        // 这里是简化版本，实际应该使用更精确的多项式
        
        double pValue;
        
        if ("c".equals(modelType)) {
            // 仅截距模型
            pValue = approximatePValueWithIntercept(tau);
        } else if ("ct".equals(modelType)) {
            // 截距 + 趋势模型
            pValue = approximatePValueWithTrend(tau);
        } else {
            // 无截距无趋势
            pValue = approximatePValueNoConstant(tau);
        }
        
        return Math.max(0.0, Math.min(1.0, pValue));
    }
    
    /**
     * 近似 p 值（仅截距）
     */
    private double approximatePValueWithIntercept(double tau) {
        // 简化的近似公式
        if (tau > -2.57) {
            return 0.10;
        } else if (tau > -2.89) {
            return 0.05;
        } else if (tau > -3.43) {
            return 0.01;
        } else {
            return 0.001;
        }
    }
    
    /**
     * 近似 p 值（截距 + 趋势）
     */
    private double approximatePValueWithTrend(double tau) {
        if (tau > -3.12) {
            return 0.10;
        } else if (tau > -3.41) {
            return 0.05;
        } else if (tau > -3.96) {
            return 0.01;
        } else {
            return 0.001;
        }
    }
    
    /**
     * 近似 p 值（无截距无趋势）
     */
    private double approximatePValueNoConstant(double tau) {
        if (tau > -1.60) {
            return 0.10;
        } else if (tau > -1.95) {
            return 0.05;
        } else if (tau > -2.60) {
            return 0.01;
        } else {
            return 0.001;
        }
    }
    
    /**
     * 获取临界值
     */
    private double[] getCriticalValues(String modelType) {
        // 返回 1%, 5%, 10% 显著性水平的临界值
        if ("c".equals(modelType)) {
            return new double[]{-3.43, -2.86, -2.57};
        } else if ("ct".equals(modelType)) {
            return new double[]{-3.96, -3.41, -3.12};
        } else {
            return new double[]{-2.60, -1.95, -1.60};
        }
    }
    
    /**
     * ADF 检验结果
     */
    public static class ADFResult {
        private final double testStatistic;
        private final double pValue;
        private final double[] criticalValues;
        private final int lag;
        
        public ADFResult(double testStatistic, double pValue, double[] criticalValues, int lag) {
            this.testStatistic = testStatistic;
            this.pValue = pValue;
            this.criticalValues = criticalValues;
            this.lag = lag;
        }
        
        /**
         * 判断是否平稳
         * 
         * @param significanceLevel 显著性水平（如 0.05）
         * @return true 如果拒绝原假设（序列平稳）
         */
        public boolean isStationary(double significanceLevel) {
            return pValue < significanceLevel;
        }
        
        public double getTestStatistic() {
            return testStatistic;
        }
        
        public double getPValue() {
            return pValue;
        }
        
        public double getCriticalValue(double significanceLevel) {
            if (significanceLevel <= 0.01) {
                return criticalValues[0];
            } else if (significanceLevel <= 0.05) {
                return criticalValues[1];
            } else {
                return criticalValues[2];
            }
        }
        
        public int getLag() {
            return lag;
        }
        
        @Override
        public String toString() {
            return String.format("ADF Test Result: statistic=%.4f, p-value=%.4f, lag=%d", 
                               testStatistic, pValue, lag);
        }
    }
}
