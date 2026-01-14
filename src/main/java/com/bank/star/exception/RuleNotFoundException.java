package com.bank.star.exception;

import java.util.UUID;

/**
 * Исключение когда правило не найдено
 */
public class RuleNotFoundException extends RuntimeException {

    private final UUID productId;

    public RuleNotFoundException(UUID productId) {
        super("Правило не найдено для productId: " + productId);
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
}