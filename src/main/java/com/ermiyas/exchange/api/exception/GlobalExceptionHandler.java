package com.ermiyas.exchange.api.exception;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExchangeException.class)
    public ResponseEntity<ExchangeDtos.ErrorResponse> handleExchangeException(ExchangeException ex) {
        return ResponseEntity.badRequest().body(new ExchangeDtos.ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName(), System.currentTimeMillis()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExchangeDtos.ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ExchangeDtos.ErrorResponse(ex.getMessage(), "IllegalArgument", System.currentTimeMillis()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExchangeDtos.ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExchangeDtos.ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName(), System.currentTimeMillis()));
    }
}
