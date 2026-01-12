package com.bank.star.controller;

import com.bank.star.dto.ResponseDTO;
import com.bank.star.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST-контроллер для работы с банковскими рекомендациями.
 * Контроллер является плоским и не содержит бизнес-логики.
 */
@RestController
@Tag(name = "Recommendation API", description = "API для получения рекомендаций банковских продуктов")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Возвращает список рекомендаций для клиента.
     */
    @GetMapping("/recommendation/{user_id}")
    @Operation(summary = "Получить рекомендацию продуктов",
            description = "Возвращает список рекомендованных банковских продуктов для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "400", description = "Неверный формат UUID пользователя"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseDTO getRecommendations(
            @Parameter(description = "Идентификатор пользователя в формате UUID",
                    example = "cd515076-5d8a-44be-930e-8d4fcb79f42d")
            @PathVariable("user_id") UUID userId) {

        log.info("Received recommendation request for user_id: {}", userId);
        return recommendationService.getRecommendationsResponse(userId);
    }
}
