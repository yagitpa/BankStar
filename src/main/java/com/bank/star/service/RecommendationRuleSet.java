package com.bank.star.service;

import com.bank.star.dto.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * Контракт для набора правил рекомендаций.
 * <p>
 * Используется для инкапсуляции бизнес-логики
 * определения применимости банковского продукта.
 * </p>
 */
public interface RecommendationRuleSet {
    /**
     * Проверяет, подходит ли продукт для пользователя
     *
     * @param userId идентификатор пользователя
     * @return Optional с рекомендацией, если правило выполняется
     */
    Optional<RecommendationDTO> check(UUID userId);
}
