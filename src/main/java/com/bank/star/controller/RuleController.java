package com.bank.star.controller;

import com.bank.star.dto.DynamicRuleRequestDTO;
import com.bank.star.dto.DynamicRuleResponseDTO;
import com.bank.star.exception.RuleNotFoundException;
import com.bank.star.service.DynamicRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций
 */
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dynamic Rule API", description = "API для управления динамическими правилами рекомендаций")
public class RuleController {

    private final DynamicRuleService dynamicRuleService;

    /**
     * Создать новое динамическое правило
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новое правило")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Правило успешно создано"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "409", description = "Правило уже существует")
    })
    public DynamicRuleResponseDTO.DynamicRuleDTO createRule(
            @Parameter(description = "Данные для создания правила")
            @Valid @RequestBody DynamicRuleRequestDTO request) {

        log.info("Creating new dynamic rule for product: {}", request.getProductName());
        return dynamicRuleService.convertToDTO(dynamicRuleService.createRule(request));
    }

    /**
     * Получить все динамические правила
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все правила")
    @ApiResponse(responseCode = "200", description = "Список правил успешно получен")
    public DynamicRuleResponseDTO getAllRules() {

        log.info("Fetching all dynamic rules");
        DynamicRuleResponseDTO response = new DynamicRuleResponseDTO();
        response.setData(dynamicRuleService.getAllRules().stream()
                                           .map(dynamicRuleService::convertToDTO)
                                           .toList());

        return response;
    }

    /**
     * Удалить правило по product_id
     */
    @DeleteMapping("/{product_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить правило")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Правило успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Правило не найдено")
    })
    public void deleteRule(
            @Parameter(description = "Идентификатор продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
            @PathVariable("product_id") UUID productId) {

        log.info("Deleting dynamic rule for productId: {}", productId);

        if (!dynamicRuleService.existsByProductId(productId)) {
            throw new RuleNotFoundException(productId);
        }

        dynamicRuleService.deleteRuleByProductId(productId);
    }
}