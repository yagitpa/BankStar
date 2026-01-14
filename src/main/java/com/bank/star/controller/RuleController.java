package com.bank.star.controller;

import com.bank.star.dto.DynamicRuleRequestDTO;
import com.bank.star.dto.DynamicRuleResponseDTO;
import com.bank.star.entity.DynamicRuleEntity;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций
 */
@RestController
@RequestMapping("/rule")
@Tag(name = "Dynamic Rule API", description = "API для управления динамическими правилами рекомендаций")
@RequiredArgsConstructor
@Slf4j
public class RuleController {

    private final DynamicRuleService dynamicRuleService;

    /**
     * Создать новое динамическое правило
     */
    @PostMapping
    @Operation(summary = "Создать новое правило",
            description = "Добавляет новое динамическое правило рекомендаций")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Правило успешно создано"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    public ResponseEntity<DynamicRuleResponseDTO.DynamicRuleDTO> createRule(
            @Parameter(description = "Данные для создания правила")
            @Valid @RequestBody DynamicRuleRequestDTO request) {

        log.info("Creating new dynamic rule for product: {}", request.getProductName());
        DynamicRuleEntity entity = dynamicRuleService.createRule(request);
        DynamicRuleResponseDTO.DynamicRuleDTO response = dynamicRuleService.convertToDTO(entity);

        return ResponseEntity.ok(response);
    }

    /**
     * Получить все динамические правила
     */
    @GetMapping
    @Operation(summary = "Получить все правила",
            description = "Возвращает список всех динамических правил рекомендаций")
    @ApiResponse(responseCode = "200", description = "Список правил успешно получен")
    public ResponseEntity<DynamicRuleResponseDTO> getAllRules() {

        log.info("Fetching all dynamic rules");
        List<DynamicRuleEntity> rules = dynamicRuleService.getAllRules();

        DynamicRuleResponseDTO response = new DynamicRuleResponseDTO();
        List<DynamicRuleResponseDTO.DynamicRuleDTO> ruleDTOs = rules.stream()
                                                                    .map(dynamicRuleService::convertToDTO)
                                                                    .toList();
        response.setData(ruleDTOs);

        return ResponseEntity.ok(response);
    }

    /**
     * Удалить правило по product_id
     */
    @DeleteMapping("/{product_id}")
    @Operation(summary = "Удалить правило",
            description = "Удаляет динамическое правило по идентификатору продукта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Правило успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Правило не найдено")
    })
    public ResponseEntity<Void> deleteRule(
            @Parameter(description = "Идентификатор продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
            @PathVariable("product_id") UUID productId) {

        log.info("Deleting dynamic rule for productId: {}", productId);
        dynamicRuleService.deleteRuleByProductId(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}