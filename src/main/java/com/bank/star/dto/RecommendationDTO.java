package com.bank.star.dto;

import java.util.UUID;

/**
 * DTO для представления рекомендации продукта
 */
public class RecommendationDTO {
    private UUID id;
    private String name;
    private String text;

    public RecommendationDTO() {
        // пустой конструктор
    }

    public RecommendationDTO(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
