package com.bank.star.service;

import com.bank.star.dto.DynamicRuleRequestDTO;
import com.bank.star.dto.DynamicRuleResponseDTO;
import com.bank.star.entity.DynamicRuleEntity;
import com.bank.star.exception.RuleAlreadyExistsException;
import com.bank.star.exception.RuleNotFoundException;
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

    @Cacheable("allRules")
    public List<DynamicRuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }

    /**
     * Формирует ResponseDTO для получения всех правил
     */
    public DynamicRuleResponseDTO getAllRulesResponse() {
        DynamicRuleResponseDTO response = new DynamicRuleResponseDTO();
        response.setData(
                getAllRules().stream()
                             .map(this::convertToDTO)
                             .toList()
        );
        return response;
    }

    @Transactional
    @CacheEvict(value = "allRules", allEntries = true)
    public DynamicRuleResponseDTO.DynamicRuleDTO createRuleAndConvert(DynamicRuleRequestDTO request) {
        if (ruleRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuleAlreadyExistsException(request.getProductId());
        }

        DynamicRuleEntity entity = ruleRepository.save(fromRequest(request));
        return convertToDTO(entity);
    }

    @Transactional
    @CacheEvict(value = {"allRules", "ruleByProductId"}, allEntries = true)
    public void deleteRuleWithCheck(UUID productId) {
        if (ruleRepository.findByProductId(productId).isEmpty()) {
            throw new RuleNotFoundException(productId);
        }
        ruleRepository.deleteByProductId(productId);
    }

    /**
     * Преобразование Entity → DTO
     */
    private DynamicRuleResponseDTO.DynamicRuleDTO convertToDTO(DynamicRuleEntity entity) {
        DynamicRuleResponseDTO.DynamicRuleDTO dto = new DynamicRuleResponseDTO.DynamicRuleDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductText(entity.getProductText());

        if (entity.getRule() != null) {
            dto.setRule(
                    entity.getRule().stream()
                          .map(this::convertConditionToDTO)
                          .toList()
            );
        }

        return dto;
    }

    /**
     * Преобразование QueryCondition Entity → DTO
     */
    private DynamicRuleRequestDTO.QueryConditionDTO convertConditionToDTO(DynamicRuleEntity.QueryCondition condition) {
        DynamicRuleRequestDTO.QueryConditionDTO dto = new DynamicRuleRequestDTO.QueryConditionDTO();
        dto.setQuery(condition.getQuery());
        dto.setArguments(condition.getArguments());
        dto.setNegate(condition.isNegate());
        return dto;
    }

    /**
     * Преобразование RequestDTO → Entity
     */
    private DynamicRuleEntity fromRequest(DynamicRuleRequestDTO request) {
        DynamicRuleEntity entity = new DynamicRuleEntity();
        entity.setProductId(request.getProductId());
        entity.setProductName(request.getProductName());
        entity.setProductText(request.getProductText());

        if (request.getRule() != null) {
            entity.setRule(
                    request.getRule().stream()
                           .map(dto -> {
                               DynamicRuleEntity.QueryCondition condition =
                                       new DynamicRuleEntity.QueryCondition();
                               condition.setQuery(dto.getQuery());
                               condition.setArguments(dto.getArguments());
                               condition.setNegate(dto.isNegate());
                               return condition;
                           })
                           .toList()
            );
        }

        return entity;
    }
}