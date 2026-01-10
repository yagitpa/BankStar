package com.bank.star.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * DTO для запроса создания/обновления динамического правила
 */
@Getter
@Setter
@Schema(description = "Запрос для создания динамического правила рекомендаций")
public class DynamicRuleRequestDTO {

    @Schema(description = "Название продукта", example = "Простой кредит")
    @JsonProperty("product_name")
    private String productName;

    @Schema(description = "Идентификатор продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
    @JsonProperty("product_id")
    private UUID productId;

    @Schema(description = "Описание продукта")
    @JsonProperty("product_text")
    private String productText;

    @Schema(description = "Массив условий правила")
    private List<QueryConditionDTO> rule;

    /**
     * DTO для условия запроса
     */
    @Getter
    @Setter
    @Schema(description = "Условие запроса в правиле")
    public static class QueryConditionDTO {

        @Schema(description = "Тип запроса",
        allowableValues = {"USER_OF", "ACTIVE_USER_OF", "TRANSACTION_SUM_COMPARE", "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW"},
        example = "USER_OF")
        private String query;

        @Schema(description = "Аргументы запроса")
        private List<String> arguments;

        @Schema(description = "Импортировать результат запроса", example = "false")
        private boolean negate;
    }
}
