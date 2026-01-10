package com.bank.star.controller;

import com.bank.star.dto.RecommendationDTO;
import com.bank.star.dto.ResponseDTO;
import com.bank.star.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Recommendation API", description = "API для получения рекомендаций банковских продуктов")
public class RecommendationController {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    private final RecommendationService recommendationService;

    @Autowired

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Получает рекомендации продуктов для пользователя
     *
     * @param userIdStr строковый идентификатор пользователя
     * @return ответ с рекомендациями
     */
    @GetMapping("/recommendation/{user_id}")
    @Operation(summary = "Получить рекомендацию продуктов",
            description = "Возвращает список рекомендованных банковских продуктов для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "400", description = "Неверный формат UUID пользователя")
    })
    public ResponseEntity<ResponseDTO> getRecommendations(
            @Parameter(description = "Идентификатор пользователя в формате UUID", example = "cd515076-5d8a-44be-930e-8d4fcb79f42d")
            @PathVariable("user_id") String userIdStr) {

        logger.info("Received recommendation request for user_id {}", userIdStr);

        try {
            UUID userId = UUID.fromString(userIdStr);
            List<RecommendationDTO> recommendations = recommendationService.getRecommendations(userId);
            ResponseDTO response = new ResponseDTO(userId, recommendations);
            logger.info("Returning {} recommendations for User {}", recommendations.size(), userId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Это исключение может быть как от UUID.fromString, так и от базы данных
            // Давайте проверим сообщение
            if (e.getMessage() != null && e.getMessage().contains("Invalid input")) {
                logger.error("Invalid UUID format: {}", userIdStr);
                return ResponseEntity.badRequest()
                                     .body(new ResponseDTO(null, Collections.emptyList()));
            } else {
                logger.error("Database error for user_id: {}", userIdStr, e);
                // Возвращаем пустой список, а не ошибку 500
                return ResponseEntity.ok(new ResponseDTO(null, Collections.emptyList()));
            }
        } catch (Exception e) {
            logger.error("Unexpected error processing request for user_id: {}", userIdStr, e);
            // Возвращаем пустой список вместо ошибки 500
            return ResponseEntity.ok(new ResponseDTO(null, Collections.emptyList()));
        }
    }
}
