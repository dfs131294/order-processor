package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.application.dto.ErrorResponseDTO;
import com.diego.orders.processor.application.exception.IdempotencyException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("CODE_01")
                .message("Error interno")
                .correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(Exception ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("CODE_02")
                .message("Credenciales incorrectas")
                .correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<ErrorResponseDTO> handleIdempotencyException(Exception ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code("CODE_05")
                .message(ex.getMessage())
                .correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
