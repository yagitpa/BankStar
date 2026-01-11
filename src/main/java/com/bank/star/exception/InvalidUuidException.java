package com.bank.star.exception;

/**
 * Исключение для невалидного UUID
 */
public class InvalidUuidException extends RuntimeException {

    private final String invalidUuid;

    public InvalidUuidException(String invalidUuid) {
        super("Неверный формат UUID: " + invalidUuid);
        this.invalidUuid = invalidUuid;
    }

    public String getInvalidUuid() {
        return invalidUuid;
    }
}