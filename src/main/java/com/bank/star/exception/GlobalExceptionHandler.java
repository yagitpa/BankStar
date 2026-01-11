package com.bank.star.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST API
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обработка невалидного UUID
     */
    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUuidException(InvalidUuidException ex) {
        log.warn("Invalid UUID format: {}", ex.getInvalidUuid());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Неверный формат UUID",
                ex.getMessage(),
                "/recommendation/{user_id}"
        );
    }

    /**
     * Обработка когда правило не найдено
     */
    @ExceptionHandler(RuleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRuleNotFoundException(RuleNotFoundException ex) {
        log.warn("Rule not found for productId: {}", ex.getProductId());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Правило не найдено",
                ex.getMessage(),
                "/rule/{product_id}"
        );
    }

    /**
     * Обработка когда правило уже существует
     */
    @ExceptionHandler(RuleAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleRuleAlreadyExistsException(RuleAlreadyExistsException ex) {
        log.warn("Rule already exists for productId: {}", ex.getProductId());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Конфликт данных",
                ex.getMessage(),
                "/rule"
        );
    }

    /**
     * Обработка ошибок базы данных
     */
    @ExceptionHandler(DatabaseQueryException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseQueryException(DatabaseQueryException ex) {
        log.error("Database query error", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ошибка базы данных",
                "Внутренняя ошибка сервера",
                null
        );
    }

    /**
     * Обработка IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Неверные параметры запроса",
                ex.getMessage(),
                null
        );
    }

    /**
     * Обработка MethodArgumentTypeMismatchException (например, не UUID в пути)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.warn("Method argument type mismatch: {}", ex.getMessage());

        String message = String.format(
                "Неверный тип параметра '%s'. Ожидается: %s, получено: %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                ex.getValue()
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Неверный тип параметра",
                message,
                null
        );
    }

    /**
     * Общая обработка всех остальных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера",
                "Произошла непредвиденная ошибка",
                null
        );
    }

    /**
     * Утилитный метод для построения ответа об ошибке
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message, String path) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);

        if (path != null) {
            errorResponse.put("path", path);
        }

        return ResponseEntity.status(status).body(errorResponse);
    }
}