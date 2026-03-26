package com.mecn.causal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 因果发现引擎实现
 * 
 * 支持多种因果方法的集成
 */
public class CausalEngineImpl implements CausalEngine {
    
    private final List<CausalMethod> methods;
    private final EnsembleFusionStrategy fusionStrategy;
    private final boolean parallel;
    
    public CausalEngineImpl() {
        this.methods = new ArrayList<>();
        this.fusionStrategy = new EnsembleFusionStrategy();
        this.parallel = true;
    }
    
    public CausalEngineImpl(boolean parallel) {
        this.methods = new ArrayList<>();
        this.fusionStrategy = new EnsembleFusionStrategy();
        this.parallel = parallel;
    }
    
    @Override
    public CausalResult discover(double[][] data, CausalConfig config) {
        if (methods.isEmpty()) {
            throw new IllegalStateException("No causal methods registered");
        }
        
        int N = data[0].length;
        List<CausalMatrix> matrices = new ArrayList<>(methods.size());
        
        // 准备各方法的参数
        Map<String, Object> methodParams = config != null ? config.getMethodParams() : new HashMap<>();
        
        // 并行或串行执行各因果方法
        if (parallel && config != null && config.isParallel()) {
            matrices = executeParallel(data, methodParams);
        } else {
            matrices = executeSequential(data, methodParams);
        }
        
        // 融合策略：使用混合策略（先投票再加权平均）
        List<Double> weights = new ArrayList<>();
        for (CausalMethod method : methods) {
            weights.add(method.getWeight());
        }
        
        CausalMatrix fusedMatrix;
        if (matrices.size() >= 2) {
            // 至少 2 个方法才进行融合
            fusedMatrix = fusionStrategy.hybrid(matrices, weights, 2);
        } else {
            // 只有一个方法，直接使用其结果
            fusedMatrix = matrices.get(0);
        }
        
        // 构建结果
        CausalResult result = new CausalResult(N);
        result.setAdjacencyMatrix(fusedMatrix.getMatrix());
        
        // 设置置信度矩阵（简化处理，设为 1.0）
        double[][] confidenceMatrix = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (fusedMatrix.getMatrix()[i][j] != 0) {
                    confidenceMatrix[i][j] = 1.0;
                }
            }
        }
        result.setConfidenceMatrix(confidenceMatrix);
        
        // 添加元数据
        result.addMetadata("numMethods", methods.size());
        result.addMetadata("methodNames", methods.stream().map(CausalMethod::getName).collect(java.util.stream.Collectors.toList()));
        result.addMetadata("fusionStrategy", "hybrid");
        result.addMetadata("minVotes", 2);
        
        return result;
    }
    
    /**
     * 并行执行各因果方法
     */
    private List<CausalMatrix> executeParallel(double[][] data, Map<String, Object> methodParams) {
        ExecutorService executor = Executors.newFixedThreadPool(methods.size());
        List<Future<CausalMatrix>> futures = new ArrayList<>();
        
        try {
            // 提交所有任务
            for (CausalMethod method : methods) {
                Callable<CausalMatrix> task = () -> {
                    Map<String, Object> params = extractMethodParams(methodParams, method.getName());
                    return method.compute(data, params);
                };
                futures.add(executor.submit(task));
            }
            
            // 收集结果
            List<CausalMatrix> results = new ArrayList<>();
            for (Future<CausalMatrix> future : futures) {
                results.add(future.get());
            }
            
            return results;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error executing causal methods in parallel", e);
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * 串行执行各因果方法
     */
    private List<CausalMatrix> executeSequential(double[][] data, Map<String, Object> methodParams) {
        List<CausalMatrix> results = new ArrayList<>();
        
        for (CausalMethod method : methods) {
            Map<String, Object> params = extractMethodParams(methodParams, method.getName());
            CausalMatrix matrix = method.compute(data, params);
            results.add(matrix);
        }
        
        return results;
    }
    
    /**
     * 提取特定方法的参数
     */
    private Map<String, Object> extractMethodParams(Map<String, Object> allParams, String methodName) {
        Map<String, Object> methodParams = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(methodName + ".")) {
                String paramKey = key.substring((methodName + ".").length());
                methodParams.put(paramKey, entry.getValue());
            }
        }
        
        return methodParams;
    }
    
    @Override
    public void registerMethod(CausalMethod method) {
        methods.add(method);
    }
    
    @Override
    public List<CausalMethod> getRegisteredMethods() {
        return new ArrayList<>(methods);
    }
    
    @Override
    public void unregisterMethod(String methodName) {
        methods.removeIf(m -> m.getName().equals(methodName));
    }
}
