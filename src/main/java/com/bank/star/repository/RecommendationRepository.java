package com.bank.star.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для выполнения SQL-запросов рекомендательной системы
 */
@Repository
public class RecommendationRepository {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Проверяет правила для продукта "Invest 500"
     *
     * @param userId идентификатор пользователя
     * @return true если правило выполняется
     */
    public boolean checkInvest500Rule(UUID userId) {
        try {
            String sql = """
                    SELECT EXISTS (SELECT 1 FROM public.users WHERE id = ?)
                    AND EXISTS (SELECT 1 FROM TRANSACTIONS t JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID WHERE t.USER_ID = ? AND p.TYPE = 'DEBIT')
                    AND NOT EXISTS (SELECT 1 FROM TRANSACTIONS t JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID WHERE t.USER_ID = ? AND p.TYPE = 'INVEST')
                    AND COALESCE((SELECT SUM(t.AMOUNT) FROM TRANSACTIONS t JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID WHERE t.USER_ID = ? AND p.TYPE = 'SAVING' AND t.TYPE = 'DEPOSIT'), 0) > ? AS result
                    """;

            Boolean result = jdbcTemplate.queryForObject(
                    sql,
                    Boolean.class,
                    userId,
                    userId,
                    userId,
                    userId,
                    1000);
            logger.debug("Invest500 rule check for User {}: {}", userId, result);
            return Boolean.TRUE.equals(result);

        } catch (DataAccessException e) {
            logger.error("Error checking Invest500 rule for User {}", userId, e);
            return false;
        }
    }

    /**
     * Проверяет правило для продукта Top Saving
     *
     * @param userId идентификатор пользователя
     * @return true если правило выполняется
     */
    public boolean checkTopSavingRule(UUID userId) {
        try {
            String sql = """
                    SELECT (EXISTS (SELECT 1 FROM public.users u WHERE u.id = ?)
                    AND EXISTS (SELECT 1 FROM TRANSACTIONS t JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID WHERE t.USER_ID = ? AND p.TYPE = 'DEBIT')
                    AND ((SELECT COALESCE(SUM(t.amount), 0) FROM TRANSACTIONS t JOIN PRODUCTS p ON p.id = t.product_id WHERE p.type = 'DEBIT' AND t.type = 'DEPOSIT' AND t.user_id = ?) >= ?
                    OR (SELECT COALESCE(SUM(t.amount), 0) FROM TRANSACTIONS t JOIN PRODUCTS p ON p.id = t.product_id WHERE p.type = 'SAVING' AND t.type = 'DEPOSIT' AND t.user_id = ?) >= ?)
                    AND (SELECT COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE 0 END), 0) - COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' THEN t.amount ELSE 0 END), 0) FROM transactions t JOIN products p ON p.id = t.product_id WHERE p.type = 'DEBIT' AND t.user_id = ?) > 0) AS result
                    """;

            Boolean result = jdbcTemplate.queryForObject(
                    sql,
                    Boolean.class,
                    userId,
                    userId,
                    userId,
                    50000,
                    userId,
                    50000,
                    userId
            );
            logger.debug("TopSaving rule check for User {}: {}", userId, result);
            return Boolean.TRUE.equals(result);

        } catch (DataAccessException e) {
            logger.error("Error checking TopSaving rule for User {}", userId, e);
            return false;
        }
    }

    /**
     * Проверяет правило для продукта Простой кредит
     *
     * @param userId идентификатор пользователя
     * @return true если правило выполняется
     */
    public boolean checkSimpleLoanRule(UUID userId) {
        try {
            String sql = """
                    SELECT EXISTS (SELECT NULL FROM public.users u WHERE u.id = ?)
                    AND NOT EXISTS (SELECT NULL FROM public.transactions t JOIN public.products p ON p.id = t.product_id WHERE p.TYPE = 'CREDIT' AND t.user_id = ?)
                    AND 0 < (SELECT COALESCE(SUM(CASE t.TYPE WHEN 'DEPOSIT' THEN t.amount ELSE 0 END), 0) - COALESCE(SUM(CASE t.TYPE WHEN 'WITHDRAW' THEN t.amount ELSE 0 END), 0) FROM public.transactions t JOIN public.products p ON p.id = t.product_id WHERE p.TYPE = 'DEBIT' AND t.user_id = ?)
                    AND ? < (SELECT COALESCE(SUM(t.amount), 0) FROM public.transactions t JOIN public.products p ON p.id = t.product_id WHERE p.TYPE = 'DEBIT' AND t.TYPE = 'WITHDRAW' AND t.user_id = ?) AS result
                    """;

            Boolean result = jdbcTemplate.queryForObject(
                    sql,
                    Boolean.class,
                    userId,
                    userId,
                    userId,
                    100000,
                    userId
            );
            logger.debug("SimpleLoan rule check for User {}: {}", userId, result);
            return Boolean.TRUE.equals(result);

        } catch (DataAccessException e) {
            logger.error("Error checking SimpleLoan rule for User {}", userId, e);
            return false;
        }
    }

    /**
     * Проверка USER_OF запроса
     */
    public boolean checkUserOf(UUID userId, String productType) {
        try {
            String sql = """
                    SELECT EXISTS (
                        SELECT 1 FROM TRANSACTIONS t 
                        JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                        WHERE t.USER_ID = ? AND p.TYPE = ?
                    ) AS result
                    """;

            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
            return Boolean.TRUE.equals(result);
        } catch (DataAccessException e) {
            logger.error("Error checking USER_OF query for User {} and productType {}", userId, productType, e);
            return false;
        }
    }

    /**
     * Проверка ACTIVE_USER_OF запроса
     */
    public boolean checkActiveUserOf(UUID userId, String productType) {
        try {
            String sql = """
                    SELECT COUNT(*) >= 5 AS result
                    FROM TRANSACTIONS t 
                    JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                    WHERE t.USER_ID = ? AND p.TYPE = ?
                    """;

            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
            return Boolean.TRUE.equals(result);
        } catch (DataAccessException e) {
            logger.error("Error checking ACTIVE_USER_OF query for User {} and productType {}", userId, productType, e);
            return false;
        }
    }

    /**
     * Проверка TRANSACTION_SUM_COMPARE запроса
     */
    public boolean checkTransactionSumCompare(UUID userId, String productType,
                                              String transactionType, String operator,
                                              int constant) {
        try {
            String sql = String.format("""
                    SELECT SUM(amount) %s ? AS result
                    FROM TRANSACTIONS t 
                    JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                    WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?
                    """, operator);

            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, constant, userId, productType, transactionType);
            return Boolean.TRUE.equals(result);
        } catch (DataAccessException e) {
            logger.error("Error checking TRANSACTION_SUM_COMPARE query for User {}: productType={}, transactionType={}, operator={}, constant={}",
                    userId, productType, transactionType, operator, constant, e);
            return false;
        }
    }

    /**
     * Проверка TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW запроса
     */
    public boolean checkTransactionSumCompareDepositWithdraw(UUID userId, String productType, String operator) {
        try {
            String sql = String.format("""
                    SELECT (SELECT COALESCE(SUM(amount), 0) 
                            FROM TRANSACTIONS t 
                            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                            WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = 'DEPOSIT')
                           %s
                           (SELECT COALESCE(SUM(amount), 0) 
                            FROM TRANSACTIONS t 
                            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                            WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = 'WITHDRAW') AS result
                    """, operator);

            Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType, userId, productType);
            return Boolean.TRUE.equals(result);
        } catch (DataAccessException e) {
            logger.error("Error checking TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW query for User {}: productType={}, operator={}",
                    userId, productType, operator, e);
            return false;
        }
    }
}