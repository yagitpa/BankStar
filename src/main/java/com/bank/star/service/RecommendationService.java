package com.bank.star.service;

import com.bank.star.dto.RecommendationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для получения рекомендаций продуктов "Invest 500", "Top Saving" или "Простой кредит"
 */
@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final List<RecommendationRuleSet> ruleSets;

    @Autowired

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
        logger.info("Loaded {} recommendation rule sets", ruleSets.size());
    }

    /**
     * Получает рекомендации продуктов для пользователя
     *
     * @param userId идентификатор пользователя
     * @return список рекомендованных продуктов
     */
    public List<RecommendationDTO> getRecommendations(UUID userId) {
        logger.info("Getting recommendations for User {}", userId);

        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (RecommendationRuleSet ruleSet : ruleSets) {
            Optional<RecommendationDTO> recommendation = ruleSet.check(userId);
            recommendation.ifPresent(recommendations::add);
            logger.debug("Added recommendation from {} for User {}",
                    ruleSet.getClass().getSimpleName(), userId);
        }

        logger.info("Found {} recommendations for User {}", recommendations.size(), userId);
        return recommendations;
    }
}