package com.mecn.exception;

/**
 * 网络分析层异常 — 网络构建、中心性计算、涟漪模拟
 */
public class NetworkException extends MECNException {
    public NetworkException(String message) {
        super(ErrorCode.NETWORK_BUILD_ERROR, message);
    }
    public NetworkException(String message, Throwable cause) {
        super(ErrorCode.NETWORK_BUILD_ERROR, message, cause);
    }
    public NetworkException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
