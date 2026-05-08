package com.mecn.api;

import com.mecn.exception.MECNException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class I18nExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(I18nExceptionHandler.class);

    @Autowired
    private MessageSource ms;

    @ExceptionHandler(MECNException.class)
    public ResponseEntity<Map<String, Object>> handleMECN(MECNException e, Locale locale) {
        String msg = ms.getMessage(
            "error." + e.getErrorCode().getCode(),
            new Object[]{e.getMessage()},
            e.getMessage(),
            locale
        );
        log.warn("MECNException (code={}): {}", e.getErrorCode().getCode(), msg);

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", msg);
        body.put("errorCode", e.getErrorCode().getCode());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException e, Locale locale) {
        String msg = ms.getMessage("error.param.invalid", new Object[]{e.getMessage()}, e.getMessage(), locale);
        log.warn("Invalid argument: {}", e.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", msg);
        body.put("errorCode", 3003);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e, Locale locale) {
        String msg = ms.getMessage("error.unknown", null, e.getMessage(), locale);
        log.error("Unhandled exception", e);

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("error", msg);
        body.put("errorCode", 9999);
        return ResponseEntity.internalServerError().body(body);
    }
}