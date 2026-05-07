package com.mecn.exception;

/**
 * 预处理层异常 — 平稳性检验、季节调整等
 */
public class PreprocessException extends MECNException {
    public PreprocessException(String message) {
        super(ErrorCode.PREPROCESS_ERROR, message);
    }
    public PreprocessException(String message, Throwable cause) {
        super(ErrorCode.PREPROCESS_ERROR, message, cause);
    }
}
