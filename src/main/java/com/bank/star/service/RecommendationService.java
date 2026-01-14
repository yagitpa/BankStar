package com.bank.star.service;

import com.bank.star.dto.RecommendationDTO;
import com.bank.star.entity.DynamicRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Формирует список рекомендаций для клиента
     */
    public List<RecommendationDTO> getRecommendations(UUID userId) {
        log.info("Getting recommendations for User {}", userId);

        List<RecommendationDTO> recommendations = new ArrayList<>();

        // 1. Проверить фиксированные правила
        for (RecommendationRuleSet ruleSet : ruleSets) {
            Optional<RecommendationDTO> recommendation = ruleSet.check(userId);
            recommendation.ifPresent(recommendations::add);
            log.debug("Added recommendation from {} for User {}",
                    ruleSet.getClass().getSimpleName(), userId);
        }

        // 2. Проверить динамические правила
        List<DynamicRuleEntity> dynamicRules = dynamicRuleService.getAllRules();
        for (DynamicRuleEntity rule : dynamicRules) {
            if (evaluateDynamicRule(userId, rule)) {
                RecommendationDTO recommendation = new RecommendationDTO(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                );
                recommendations.add(recommendation);
                log.info("Added dynamic rule recommendation for product: {} for User {}",
                        rule.getProductName(), userId);
            }
        }

        log.info("Found {} recommendations for User {}", recommendations.size(), userId);
        return recommendations;
    }

    /**
     * Оценить динамическое правило для пользователя
     */
    private boolean evaluateDynamicRule(UUID userId, DynamicRuleEntity rule) {
        if (rule.getRule() == null || rule.getRule().isEmpty()) {
            return false;
        }

        boolean allConditionsTrue = true;

        for (DynamicRuleEntity.QueryCondition condition : rule.getRule()) {
            boolean conditionResult = evaluateQueryCondition(userId, condition);
            if (!conditionResult) {
                allConditionsTrue = false;
                break;
            }
        }

        return allConditionsTrue;
    }

    /**
     * Оценить одно условие запроса
     */
    private boolean evaluateQueryCondition(UUID userId, DynamicRuleEntity.QueryCondition condition) {
        try {
            switch (condition.getQuery()) {
                case "USER_OF":
                    if (condition.getArguments().size() < 1) {
                        log.error("Invalid arguments for USER_OF query");
                        return false;
                    }
                    return queryService.checkUserOf(userId, condition.getArguments().get(0), condition.isNegate());

                case "ACTIVE_USER_OF":
                    if (condition.getArguments().size() < 1) {
                        log.error("Invalid arguments for ACTIVE_USER_OF query");
                        return false;
                    }
                    return queryService.checkActiveUserOf(userId, condition.getArguments().get(0), condition.isNegate());

                case "TRANSACTION_SUM_COMPARE":
                    if (condition.getArguments().size() < 4) {
                        log.error("Invalid arguments for TRANSACTION_SUM_COMPARE query");
                        return false;
                    }
                    int constant = Integer.parseInt(condition.getArguments().get(3));
                    return queryService.checkTransactionSumCompare(
                            userId,
                            condition.getArguments().get(0),
                            condition.getArguments().get(1),
                            condition.getArguments().get(2),
                            constant,
                            condition.isNegate()
                    );

                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                    if (condition.getArguments().size() < 2) {
                        log.error("Invalid arguments for TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW query");
                        return false;
                    }
                    return queryService.checkTransactionSumCompareDepositWithdraw(
                            userId,
                            condition.getArguments().get(0),
                            condition.getArguments().get(1),
                            condition.isNegate()
                    );

                default:
                    log.error("Unknown query type: {}", condition.getQuery());
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating query condition: {}", condition.getQuery(), e);
            return false;
        }
    }
}