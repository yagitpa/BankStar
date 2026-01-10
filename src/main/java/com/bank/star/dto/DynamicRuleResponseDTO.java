package com.bank.star.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * DTO для ответа API динамических правил
 */
@Getter
@Setter
@Schema(description = "Ответ с динамическими правилами")
public class DynamicRuleResponseDTO {

    @Schema(description = "Список правил")
    private List<DynamicRuleDTO> data;

    /**
     * DTO для одного правила
     */
    @Getter
    @Setter
    @Schema(description = "Динамическое правило рекомендаций")
    public static class DynamicRuleDTO {

        @Schema(description = "Идентификатор правила")
        private Long id;

        @Schema(description = "Название продукта", example = "Простой кредит")
        @JsonProperty("product_name")
        private String productName;

        @Schema(description = "Идентификатор продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
        @JsonProperty("product_id")
        private UUID productId;

        @Schema(description = "Текст рекомендации продукта")
        @JsonProperty("product_text")
        private String productText;

        @Schema(description = "Массив условий правила")
        private List<DynamicRuleRequestDTO.QueryConditionDTO> rule;
    }
}