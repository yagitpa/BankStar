package com.bank.star.exception;

/**
 * Исключение для ошибок базы данных
 */
public class DatabaseQueryException extends RuntimeException {

    public DatabaseQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseQueryException(String message) {
        super(message);
    }
}