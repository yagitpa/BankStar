package com.bank.star.controller;

import com.bank.star.dto.RecommendationDTO;
import com.bank.star.dto.ResponseDTO;
import com.bank.star.exception.InvalidUuidException;
import com.bank.star.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для работы с банковскими рекомендациями.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recommendation API", description = "API для получения рекомендаций банковских продуктов")
public class RecommendationController {

    private final RecommendationService recommendationService;

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
            @PathVariable("user_id") String userIdStr) {

        log.info("Received recommendation request for user_id: {}", userIdStr);

        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException(userIdStr);
        }

        List<RecommendationDTO> recommendations = recommendationService.getRecommendations(userId);
        log.info("Returning {} recommendations for User: {}", recommendations.size(), userId);

        return new ResponseDTO(userId, recommendations);
    }
}