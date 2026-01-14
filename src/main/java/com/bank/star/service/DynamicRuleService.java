package com.bank.star.service;

import com.bank.star.dto.DynamicRuleRequestDTO;
import com.bank.star.dto.DynamicRuleResponseDTO;
import com.bank.star.entity.DynamicRuleEntity;
import com.bank.star.exception.RuleAlreadyExistsException;
import com.bank.star.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с динамическими правилами рекомендаций
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicRuleService {

    private final RuleRepository ruleRepository;

    /**
     * Получить все динамические правила
     */
    @Cacheable(value = "allRules")
    public List<DynamicRuleEntity> getAllRules() {
        log.info("Fetching all dynamic rules from database");
        return ruleRepository.findAll();
    }

    /**
     * Создать новое правило
     */
    @Transactional
    @CacheEvict(value = "allRules", allEntries = true)
    public DynamicRuleEntity createRule(DynamicRuleRequestDTO request) {
        log.info("Creating new dynamic rule for Product: {}", request.getProductName());

        if (ruleRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuleAlreadyExistsException(request.getProductId());
        }

        DynamicRuleEntity entity = new DynamicRuleEntity();
        entity.setProductName(request.getProductName());
        entity.setProductId(request.getProductId());
        entity.setProductText(request.getProductText());

        if (request.getRule() != null) {
            List<DynamicRuleEntity.QueryCondition> conditions = request.getRule().stream()
                                                                       .map(dto -> {
                                                                           DynamicRuleEntity.QueryCondition condition = new DynamicRuleEntity.QueryCondition();
                                                                           condition.setQuery(dto.getQuery());
                                                                           condition.setArguments(dto.getArguments());
                                                                           condition.setNegate(dto.isNegate());
                                                                           return condition;
                                                                       })
                                                                       .toList();
            entity.setRule(conditions);
        }
        return ruleRepository.save(entity);
    }

    /**
     * Удалить правило по product_id
     */
    @Transactional
    @CacheEvict(value = {"allRules", "ruleByProductId"}, allEntries = true)
    public void deleteRuleByProductId(UUID productId) {
        log.info("Deleting dynamic rule for productId: {}", productId);
        ruleRepository.deleteByProductId(productId);
    }

    /**
     * Получить правило по product_id
     */
    @Cacheable(value = "ruleByProductId", key = "#productId")
    public DynamicRuleEntity getRuleByProductId(UUID productId) {
        log.info("Fetching dynamic rule for productId: {}", productId);
        return ruleRepository.findByProductId(productId)
                             .orElseThrow(() -> new IllegalArgumentException("Rule not found for productId: " + productId));
    }

    /**
     * Конвертировать сущность в DTO для ответа
     */
    public DynamicRuleResponseDTO.DynamicRuleDTO convertToDTO(DynamicRuleEntity entity) {
        DynamicRuleResponseDTO.DynamicRuleDTO dto = new DynamicRuleResponseDTO.DynamicRuleDTO();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setProductId(entity.getProductId());
        dto.setProductText(entity.getProductText());

        if (entity.getRule() != null) {
            List<DynamicRuleRequestDTO.QueryConditionDTO> conditions = entity.getRule().stream()
                                                                             .map(condition -> {
                                                                                 DynamicRuleRequestDTO.QueryConditionDTO conditionDTO = new DynamicRuleRequestDTO.QueryConditionDTO();
                                                                                 conditionDTO.setQuery(condition.getQuery());
                                                                                 conditionDTO.setArguments(condition.getArguments());
                                                                                 conditionDTO.setNegate(condition.isNegate());
                                                                                 return conditionDTO;
                                                                             })
                                                                             .toList();
            dto.setRule(conditions);
        }
        return dto;
    }

    public boolean existsByProductId(UUID productId) {
        return ruleRepository.findByProductId(productId).isPresent();
    }
}
