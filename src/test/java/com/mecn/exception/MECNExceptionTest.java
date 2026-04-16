package com.mecn.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * MECNException 单元测试
 * 
 * 测试异常处理框架的功能
 */
class MECNExceptionTest {
    
    @Test
    void testConstructorWithMessage() {
        // When
        MECNException exception = new MECNException("Test error message");
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("Test error message");
        assertThat(exception.getErrorCode()).isEqualTo(MECNException.ErrorCode.UNKNOWN_ERROR);
    }
    
    @Test
    void testConstructorWithMessageAndCause() {
        // Given
        RuntimeException cause = new RuntimeException("Original cause");
        
        // When
        MECNException exception = new MECNException("Test error", cause);
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception.getErrorCode()).isEqualTo(MECNException.ErrorCode.UNKNOWN_ERROR);
    }
    
    @Test
    void testConstructorWithErrorCodeAndMessage() {
        // When
        MECNException exception = new MECNException(
            MECNException.ErrorCode.DATA_FETCH_ERROR,
            "Failed to fetch data"
        );
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("Failed to fetch data");
        assertThat(exception.getErrorCode()).isEqualTo(MECNException.ErrorCode.DATA_FETCH_ERROR);
    }
    
    @Test
    void testConstructorWithErrorCodeMessageAndCause() {
        // Given
        RuntimeException cause = new RuntimeException("Root cause");
        
        // When
        MECNException exception = new MECNException(
            MECNException.ErrorCode.CAUSAL_DISCOVERY_ERROR,
            "Causal discovery failed",
            cause
        );
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("Causal discovery failed");
        assertThat(exception.getErrorCode()).isEqualTo(MECNException.ErrorCode.CAUSAL_DISCOVERY_ERROR);
        assertThat(exception.getCause()).isSameAs(cause);
    }
    
    @Test
    void testDataRelatedErrorCodes() {
        // Then
        assertThat(MECNException.ErrorCode.DATA_FETCH_ERROR.getCode()).isEqualTo(1001);
        assertThat(MECNException.ErrorCode.DATA_FORMAT_ERROR.getCode()).isEqualTo(1002);
        assertThat(MECNException.ErrorCode.DATA_VALIDATION_ERROR.getCode()).isEqualTo(1003);
        assertThat(MECNException.ErrorCode.MISSING_DATA_ERROR.getCode()).isEqualTo(1004);
    }
    
    @Test
    void testPreprocessErrorCodes() {
        // Then
        assertThat(MECNException.ErrorCode.PREPROCESS_ERROR.getCode()).isEqualTo(2001);
        assertThat(MECNException.ErrorCode.STATIONARITY_TEST_ERROR.getCode()).isEqualTo(2002);
        assertThat(MECNException.ErrorCode.SEASONAL_ADJUSTMENT_ERROR.getCode()).isEqualTo(2003);
    }
    
    @Test
    void testCausalAnalysisErrorCodes() {
        // Then
        assertThat(MECNException.ErrorCode.CAUSAL_DISCOVERY_ERROR.getCode()).isEqualTo(3001);
        assertThat(MECNException.ErrorCode.ALGORITHM_EXECUTION_ERROR.getCode()).isEqualTo(3002);
        assertThat(MECNException.ErrorCode.INVALID_PARAMETERS_ERROR.getCode()).isEqualTo(3003);
        assertThat(MECNException.ErrorCode.CONVERGENCE_ERROR.getCode()).isEqualTo(3004);
    }
    
    @Test
    void testNetworkAnalysisErrorCodes() {
        // Then
        assertThat(MECNException.ErrorCode.NETWORK_BUILD_ERROR.getCode()).isEqualTo(4001);
        assertThat(MECNException.ErrorCode.CENTRALITY_CALCULATION_ERROR.getCode()).isEqualTo(4002);
        assertThat(MECNException.ErrorCode.COMMUNITY_DETECTION_ERROR.getCode()).isEqualTo(4003);
        assertThat(MECNException.ErrorCode.RIPPLE_SIMULATION_ERROR.getCode()).isEqualTo(4004);
    }
    
    @Test
    void testDiagnosisErrorCodes() {
        // Then
        assertThat(MECNException.ErrorCode.DIAGNOSIS_ERROR.getCode()).isEqualTo(5001);
        assertThat(MECNException.ErrorCode.REPORT_GENERATION_ERROR.getCode()).isEqualTo(5002);
    }
    
    @Test
    void testSystemErrorCodes() {
        // Then
        assertThat(MECNException.ErrorCode.UNKNOWN_ERROR.getCode()).isEqualTo(9999);
    }
    
    @Test
    void testErrorCodeDescriptions() {
        // Then
        assertThat(MECNException.ErrorCode.DATA_FETCH_ERROR.getDescription())
            .isEqualTo("数据获取失败");
        assertThat(MECNException.ErrorCode.CAUSAL_DISCOVERY_ERROR.getDescription())
            .isEqualTo("因果发现失败");
        assertThat(MECNException.ErrorCode.UNKNOWN_ERROR.getDescription())
            .isEqualTo("未知错误");
    }
    
    @Test
    void testExceptionIsRuntimeException() {
        // When & Then
        assertThat(new MECNException("test"))
            .isInstanceOf(RuntimeException.class);
    }
    
    @Test
    void testThrowingAndCatchingException() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new MECNException(
                MECNException.ErrorCode.DATA_VALIDATION_ERROR,
                "Invalid data format"
            );
        })
            .isInstanceOf(MECNException.class)
            .hasMessage("Invalid data format");
    }
}
