package com.bank.star.controller;

import com.bank.star.dto.DynamicRuleRequestDTO;
import com.bank.star.dto.DynamicRuleResponseDTO;
import com.bank.star.service.DynamicRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 * Не содержит бизнес-логики.
 */
@RestController
@RequestMapping("/rule")
@Tag(name = "Dynamic Rule API", description = "API для управления динамическими правилами рекомендаций")
public class RuleController {

    private static final Logger log = LoggerFactory.getLogger(RuleController.class);

    private final DynamicRuleService dynamicRuleService;

    public RuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новое правило")
    public DynamicRuleResponseDTO.DynamicRuleDTO createRule(
            @Valid @RequestBody DynamicRuleRequestDTO request) {

        log.info("Creating new dynamic rule");
        return dynamicRuleService.createRuleAndConvert(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все правила")
    public DynamicRuleResponseDTO getAllRules() {
        return dynamicRuleService.getAllRulesResponse();
    }

    @DeleteMapping("/{product_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить правило")
    public void deleteRule(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("product_id") UUID productId) {

        dynamicRuleService.deleteRuleWithCheck(productId);
    }
}