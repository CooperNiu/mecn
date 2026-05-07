package com.mecn.exception;

/**
 * 因果发现层异常 — 算法执行失败、参数无效、不收敛
 */
public class CausalDiscoveryException extends MECNException {
    public CausalDiscoveryException(String message) {
        super(ErrorCode.CAUSAL_DISCOVERY_ERROR, message);
    }
    public CausalDiscoveryException(String message, Throwable cause) {
        super(ErrorCode.CAUSAL_DISCOVERY_ERROR, message, cause);
    }
    public CausalDiscoveryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
