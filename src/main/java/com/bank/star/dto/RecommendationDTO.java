package com.bank.star.dto;

import lombok.*;

import java.util.UUID;

/**
 * DTO для представления рекомендации продукта
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDTO {
    private UUID id;
    private String name;
    private String text;
}
