package com.bank.star.service;

import com.bank.star.dto.RecommendationDTO;
import com.bank.star.dto.ResponseDTO;
import com.bank.star.entity.DynamicRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для получения рекомендаций продуктов
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;
    private final DynamicRuleService dynamicRuleService;
    private final QueryService queryService;

    /**
     * Публичный метод для контроллера.
     * Формирует полный ResponseDTO.
     */
    public ResponseDTO getRecommendationsResponse(UUID userId) {
        List<RecommendationDTO> recommendations = getRecommendations(userId);
        return new ResponseDTO(userId, recommendations);
    }

    /**
     * Формирует список рекомендаций для клиента
     */
    private List<RecommendationDTO> getRecommendations(UUID userId) {
        log.info("Getting recommendations for User {}", userId);

        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (RecommendationRuleSet ruleSet : ruleSets) {
            Optional<RecommendationDTO> recommendation = ruleSet.check(userId);
            recommendation.ifPresent(recommendations::add);
        }

        List<DynamicRuleEntity> dynamicRules = dynamicRuleService.getAllRules();
        for (DynamicRuleEntity rule : dynamicRules) {
            if (evaluateDynamicRule(userId, rule)) {
                recommendations.add(
                        new RecommendationDTO(
                                rule.getProductId(),
                                rule.getProductName(),
                                rule.getProductText()
                        )
                );
            }
        }

        return recommendations;
    }

    private boolean evaluateDynamicRule(UUID userId, DynamicRuleEntity rule) {
        if (rule.getRule() == null || rule.getRule().isEmpty()) {
            return false;
        }

        for (DynamicRuleEntity.QueryCondition condition : rule.getRule()) {
            if (!evaluateQueryCondition(userId, condition)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateQueryCondition(UUID userId, DynamicRuleEntity.QueryCondition condition) {
        try {
            return switch (condition.getQuery()) {
                case "USER_OF" ->
                        queryService.checkUserOf(userId, condition.getArguments().get(0), condition.isNegate());
                case "ACTIVE_USER_OF" ->
                        queryService.checkActiveUserOf(userId, condition.getArguments().get(0), condition.isNegate());
                default -> false;
            };
        } catch (Exception e) {
            log.error("Error evaluating query condition", e);
            return false;
        }
    }
}
