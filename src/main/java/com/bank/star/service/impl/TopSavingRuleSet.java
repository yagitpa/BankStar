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
 * Реализация правила для продукта Top Saving
 */
@Component
public class TopSavingRuleSet implements RecommendationRuleSet {
    private static final Logger logger = LoggerFactory.getLogger(TopSavingRuleSet.class);

    private static final UUID PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String PRODUCT_NAME = "Top Saving";
    private static final String PRODUCT_TEXT = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент," +
            "который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под " +
            "контролем! Преимущества «Копилки»: Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет " +
            "автоматически переводить определенную сумму на ваш счет. Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте " +
            "процесс накопления и корректируйте стратегию при необходимости. Безопасность и надежность. Ваши средства находятся под защитой банка, " +
            "а доступ к ним возможен только через мобильное приложение или интернет-банкинг. Начните использовать «Копилку» уже сегодня и станьте " +
            "ближе к своим финансовым целям!";

    private final RecommendationRepository repository;

    @Autowired

    public TopSavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        logger.info("Checking TopSaving rule for User {}", userId);

        if (repository.checkTopSavingRule(userId)) {
            logger.info("TopSaving rule passed for User {}", userId);
            return Optional.of(new RecommendationDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_TEXT));
        }

        logger.debug("TopSaving rule failed for User {}", userId);
        return Optional.empty();
    }
}
