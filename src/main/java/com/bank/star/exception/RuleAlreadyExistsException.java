package com.bank.star.exception;

import java.util.UUID;

/**
 * Исключение когда правило уже существует
 */
public class RuleAlreadyExistsException extends RuntimeException {

    private final UUID productId;

    public RuleAlreadyExistsException(UUID productId) {
        super("Правило уже существует для productId: " + productId);
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
}