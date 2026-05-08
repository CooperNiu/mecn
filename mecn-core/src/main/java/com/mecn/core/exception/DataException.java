package com.mecn.exception;

/**
 * 数据层异常 — 数据获取、格式、验证
 */
public class DataException extends MECNException {
    public DataException(String message) {
        super(ErrorCode.DATA_FETCH_ERROR, message);
    }
    public DataException(String message, Throwable cause) {
        super(ErrorCode.DATA_FETCH_ERROR, message, cause);
    }
    public DataException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
