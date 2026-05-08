package com.mecn.exception;

/**
 * 报告生成层异常
 */
public class ReportException extends MECNException {
    public ReportException(String message) {
        super(ErrorCode.REPORT_GENERATION_ERROR, message);
    }
    public ReportException(String message, Throwable cause) {
        super(ErrorCode.REPORT_GENERATION_ERROR, message, cause);
    }
}
