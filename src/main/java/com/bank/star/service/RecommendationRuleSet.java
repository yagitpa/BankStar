package com.bank.star.service;

import com.bank.star.dto.RecommendationDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс для стратегии проверки правил рекомендаций
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
