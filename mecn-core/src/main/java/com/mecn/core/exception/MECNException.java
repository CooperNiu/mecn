package com.mecn.exception;

/**
 * MECN 基础异常类
 * 
 * 所有 MECN 自定义异常的父类
 */
public class MECNException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public MECNException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }
    
    public MECNException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }
    
    public MECNException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public MECNException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * 错误码枚举
     */
    public enum ErrorCode {
        // 数据相关错误 (1xxx)
        DATA_FETCH_ERROR(1001, "数据获取失败"),
        DATA_FORMAT_ERROR(1002, "数据格式错误"),
        DATA_VALIDATION_ERROR(1003, "数据验证失败"),
        MISSING_DATA_ERROR(1004, "数据缺失"),
        
        // 预处理相关错误 (2xxx)
        PREPROCESS_ERROR(2001, "数据预处理失败"),
        STATIONARITY_TEST_ERROR(2002, "平稳性检验失败"),
        SEASONAL_ADJUSTMENT_ERROR(2003, "季节性调整失败"),
        
        // 因果分析相关错误 (3xxx)
        CAUSAL_DISCOVERY_ERROR(3001, "因果发现失败"),
        ALGORITHM_EXECUTION_ERROR(3002, "算法执行失败"),
        INVALID_PARAMETERS_ERROR(3003, "参数无效"),
        CONVERGENCE_ERROR(3004, "算法未收敛"),
        
        // 网络分析相关错误 (4xxx)
        NETWORK_BUILD_ERROR(4001, "网络构建失败"),
        CENTRALITY_CALCULATION_ERROR(4002, "中心性计算失败"),
        COMMUNITY_DETECTION_ERROR(4003, "社区检测失败"),
        RIPPLE_SIMULATION_ERROR(4004, "涟漪模拟失败"),
        
        // 诊断相关错误 (5xxx)
        DIAGNOSIS_ERROR(5001, "诊断分析失败"),
        REPORT_GENERATION_ERROR(5002, "报告生成失败"),
        
        // 系统相关错误 (9xxx)
        UNKNOWN_ERROR(9999, "未知错误");
        
        private final int code;
        private final String description;
        
        ErrorCode(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
