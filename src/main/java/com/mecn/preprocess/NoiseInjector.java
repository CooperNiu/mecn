package com.mecn.preprocess;

import java.util.Random;

/**
 * 噪声注入器 - 用于测试算法的鲁棒性
 * 
 * 向时间序列数据中添加各种类型的噪声，评估算法在噪声环境下的表现
 */
public class NoiseInjector {
    
    private final Random random;
    
    public NoiseInjector() {
        this.random = new Random(42);  // 固定种子以保证可重复性
    }
    
    public NoiseInjector(long seed) {
        this.random = new Random(seed);
    }
    
    /**
     * 噪声类型
     */
    public enum NoiseType {
        GAUSSIAN,       // 高斯噪声
        UNIFORM,        // 均匀噪声
        IMPULSE,        // 脉冲噪声（尖峰）
        DRIFT,          // 漂移噪声
        SEASONAL        // 季节性噪声
    }
    
    /**
     * 添加高斯噪声
     * 
     * @param data 原始数据
     * @param stdDev 标准差（相对于数据标准差的比例）
     * @return 带噪声的数据
     */
    public double[][] addGaussianNoise(double[][] data, double stdDev) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        int T = data.length;
        int N = data[0].length;
        double[][] noisyData = copyData(data);
        
        // 计算每个变量的标准差
        double[] stds = calculateStdDevs(data);
        
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                double noise = random.nextGaussian() * stdDev * stds[i];
                noisyData[t][i] += noise;
            }
        }
        
        return noisyData;
    }
    
    /**
     * 添加均匀分布噪声
     * 
     * @param data 原始数据
     * @param amplitude 噪声幅度（相对于数据范围的比例）
     * @return 带噪声的数据
     */
    public double[][] addUniformNoise(double[][] data, double amplitude) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        int T = data.length;
        int N = data[0].length;
        double[][] noisyData = copyData(data);
        
        // 计算每个变量的范围
        double[] ranges = calculateRanges(data);
        
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                double noise = (random.nextDouble() - 0.5) * 2 * amplitude * ranges[i];
                noisyData[t][i] += noise;
            }
        }
        
        return noisyData;
    }
    
    /**
     * 添加脉冲噪声（随机尖峰）
     * 
     * @param data 原始数据
     * @param probability 每个点出现脉冲的概率
     * @param magnitude 脉冲幅度倍数
     * @return 带噪声的数据
     */
    public double[][] addImpulseNoise(double[][] data, double probability, double magnitude) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        int T = data.length;
        int N = data[0].length;
        double[][] noisyData = copyData(data);
        
        double[] stds = calculateStdDevs(data);
        
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                if (random.nextDouble() < probability) {
                    // 添加正向或负向脉冲
                    double sign = random.nextBoolean() ? 1.0 : -1.0;
                    double impulse = sign * magnitude * stds[i];
                    noisyData[t][i] += impulse;
                }
            }
        }
        
        return noisyData;
    }
    
    /**
     * 添加漂移噪声（趋势性偏移）
     * 
     * @param data 原始数据
     * @param driftRate 每时间步的漂移率
     * @return 带噪声的数据
     */
    public double[][] addDriftNoise(double[][] data, double driftRate) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        int T = data.length;
        int N = data[0].length;
        double[][] noisyData = copyData(data);
        
        double[] stds = calculateStdDevs(data);
        
        for (int i = 0; i < N; i++) {
            double cumulativeDrift = 0.0;
            for (int t = 0; t < T; t++) {
                cumulativeDrift += driftRate * stds[i];
                noisyData[t][i] += cumulativeDrift;
            }
        }
        
        return noisyData;
    }
    
    /**
     * 添加季节性噪声
     * 
     * @param data 原始数据
     * @param period 季节周期
     * @param amplitude 季节性振幅（相对于标准差的比例）
     * @return 带噪声的数据
     */
    public double[][] addSeasonalNoise(double[][] data, int period, double amplitude) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
        
        int T = data.length;
        int N = data[0].length;
        double[][] noisyData = copyData(data);
        
        double[] stds = calculateStdDevs(data);
        
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                // 正弦季节性模式
                double seasonalComponent = amplitude * stds[i] * 
                    Math.sin(2 * Math.PI * t / period);
                noisyData[t][i] += seasonalComponent;
            }
        }
        
        return noisyData;
    }
    
    /**
     * 添加混合噪声（多种噪声组合）
     * 
     * @param data 原始数据
     * @param gaussianStd 高斯噪声标准差比例
     * @param impulseProb 脉冲噪声概率
     * @param driftRate 漂移率
     * @return 带混合噪声的数据
     */
    public double[][] addMixedNoise(double[][] data, double gaussianStd, 
                                     double impulseProb, double driftRate) {
        double[][] result = copyData(data);
        
        // 依次添加各种噪声
        result = addGaussianNoise(result, gaussianStd);
        result = addImpulseNoise(result, impulseProb, 3.0);
        result = addDriftNoise(result, driftRate);
        
        return result;
    }
    
    /**
     * 计算信噪比 (SNR)
     * 
     * @param original 原始数据
     * @param noisy 带噪声的数据
     * @return SNR (dB)
     */
    public double calculateSNR(double[][] original, double[][] noisy) {
        if (original == null || noisy == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        int T = original.length;
        int N = original[0].length;
        
        double signalPower = 0.0;
        double noisePower = 0.0;
        
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                signalPower += original[t][i] * original[t][i];
                double noise = noisy[t][i] - original[t][i];
                noisePower += noise * noise;
            }
        }
        
        if (noisePower < 1e-10) {
            return Double.POSITIVE_INFINITY;
        }
        
        // SNR = 10 * log10(signal_power / noise_power)
        return 10 * Math.log10(signalPower / noisePower);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 复制数据数组
     */
    private double[][] copyData(double[][] data) {
        int T = data.length;
        int N = data[0].length;
        double[][] copy = new double[T][N];
        
        for (int t = 0; t < T; t++) {
            System.arraycopy(data[t], 0, copy[t], 0, N);
        }
        
        return copy;
    }
    
    /**
     * 计算每个变量的标准差
     */
    private double[] calculateStdDevs(double[][] data) {
        int T = data.length;
        int N = data[0].length;
        double[] stds = new double[N];
        
        for (int i = 0; i < N; i++) {
            double mean = 0.0;
            for (int t = 0; t < T; t++) {
                mean += data[t][i];
            }
            mean /= T;
            
            double variance = 0.0;
            for (int t = 0; t < T; t++) {
                variance += Math.pow(data[t][i] - mean, 2);
            }
            variance /= T;
            
            stds[i] = Math.sqrt(variance);
            
            // 避免除零
            if (stds[i] < 1e-10) {
                stds[i] = 1.0;
            }
        }
        
        return stds;
    }
    
    /**
     * 计算每个变量的范围
     */
    private double[] calculateRanges(double[][] data) {
        int T = data.length;
        int N = data[0].length;
        double[] ranges = new double[N];
        
        for (int i = 0; i < N; i++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            
            for (int t = 0; t < T; t++) {
                min = Math.min(min, data[t][i]);
                max = Math.max(max, data[t][i]);
            }
            
            ranges[i] = max - min;
            
            // 避免除零
            if (ranges[i] < 1e-10) {
                ranges[i] = 1.0;
            }
        }
        
        return ranges;
    }
}
