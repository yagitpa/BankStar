package com.bank.star.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * DTO для ответа рекомендательной системы
 */
public class ResponseDTO {

    @JsonProperty("user_id")
    private UUID userId;
    private List<RecommendationDTO> recommendations;

    public ResponseDTO() {
        // пустой конструктор
    }

    public ResponseDTO(UUID userId, List<RecommendationDTO> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<RecommendationDTO> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationDTO> recommendations) {
        this.recommendations = recommendations;
    }
}
