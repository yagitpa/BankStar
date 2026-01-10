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
 * Реализация правила для продукта Invest 500
 */
@Component
public class Invest500RuleSet implements RecommendationRuleSet {
    private static final Logger logger = LoggerFactory.getLogger(Invest500RuleSet.class);

    private static final UUID PRODUCT_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String PRODUCT_NAME = "Invest 500";
    private static final String PRODUCT_TEXT = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
            "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на " +
            "взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными " +
            "рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    private final RecommendationRepository repository;

    @Autowired

    public Invest500RuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        logger.info("Checking Invest500 rule for User {}", userId);

        if (repository.checkInvest500Rule(userId)) {
            logger.info("Invest500 rule passed for User {}", userId);
            return Optional.of(new RecommendationDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_TEXT));
        }

        logger.debug("Invest500 rule failed for User {}", userId);
        return Optional.empty();
    }
}
