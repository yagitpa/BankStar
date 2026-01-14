package com.bank.star.service;

import com.bank.star.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис для выполнения запросов динамических правил с кэшированием
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueryService {

    private final RecommendationRepository recommendationRepository;

    /**
     * Проверка USER_OF запроса с кэшированием
     */
    @Cacheable(value = "userOfQuery", key = "#userId.toString() + '_' + #productType")
    public boolean checkUserOf(UUID userId, String productType, boolean negate) {
        log.debug("Checking USER_OF query for userId: {}, productType: {}", userId, productType);
        boolean result = recommendationRepository.checkUserOf(userId, productType);
        return negate != result;
    }

    /**
     * Проверка ACTIVE_USER_OF запроса с кэшированием
     */
    @Cacheable(value = "activeUserOfQuery", key = "#userId.toString() + '_' + #productType")
    public boolean checkActiveUserOf(UUID userId, String productType, boolean negate) {
        log.debug("Checking ACTIVE_USER_OF query for userId: {}, productType: {}", userId, productType);
        boolean result = recommendationRepository.checkActiveUserOf(userId, productType);
        return negate != result;
    }

    /**
     * Проверка TRANSACTION_SUM_COMPARE запроса с кэшированием
     */
    @Cacheable(value = "transactionSumCompareQuery",
            key = "#userId.toString() + '_' + #productType + '_' + #transactionType + '_' + #operator + '_' + #constant")
    public boolean checkTransactionSumCompare(UUID userId, String productType,
                                              String transactionType, String operator,
                                              int constant, boolean negate) {
        log.debug("Checking TRANSACTION_SUM_COMPARE for userId: {}, productType: {}, transactionType: {}, operator: {}, constant: {}",
                userId, productType, transactionType, operator, constant);
        boolean result = recommendationRepository.checkTransactionSumCompare(userId, productType,
                transactionType, operator, constant);
        return negate != result;
    }

    /**
     * Проверка TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW запроса с кэшированием
     */
    @Cacheable(value = "transactionSumCompareDepositWithdrawQuery",
            key = "#userId.toString() + '_' + #productType + '_' + #operator")
    public boolean checkTransactionSumCompareDepositWithdraw(UUID userId, String productType,
                                                             String operator, boolean negate) {
        log.debug("Checking TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW for userId: {}, productType: {}, operator: {}",
                userId, productType, operator);
        boolean result = recommendationRepository.checkTransactionSumCompareDepositWithdraw(userId, productType, operator);
        return negate != result;
    }
}
