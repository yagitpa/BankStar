package com.bank.star.service.impl;

import com.bank.star.dto.RecommendationDTO;
import com.bank.star.repository.RecommendationRepository;
import com.bank.star.service.RecommendationRuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Реализация правила для продукта "Простой кредит"
 */
@Component
public class SimpleLoanRuleSet implements RecommendationRuleSet {
    private static final Logger logger = LoggerFactory.getLogger(SimpleLoanRuleSet.class);

    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String PRODUCT_NAME = "Простой кредит";
    private static final String PRODUCT_TEXT = "Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить нужную " +
            "сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный " +
            "подход к каждому клиенту. Почему выбирают нас: Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки " +
            "занимает всего несколько часов. Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении. " +
            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образования, лечения и " +
            "многое другое. Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    private final RecommendationRepository repository;

    @Autowired

    public SimpleLoanRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        logger.info("Checking SimpleLoan rule for User {}", userId);

        if (repository.checkSimpleLoanRule(userId)) {
            logger.info("SimpleLoan rule passed for User {}", userId);
            return Optional.of(new RecommendationDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_TEXT));
        }

        logger.debug("SimpleLoan rule failed for User {}", userId);
        return Optional.empty();
    }
}
