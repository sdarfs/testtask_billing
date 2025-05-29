package com.billing.testtask.controller;

import com.billing.testtask.entity.TypeEntity;
import com.billing.testtask.service.TypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * REST контроллер для управления типами задач.
 * Предоставляет операции для работы с типами задач, включая их получение с различными параметрами сортировки.
 */
@RestController
@RequestMapping("/types")
@RequiredArgsConstructor
@Tag(name = "Type API", description = "Операции с типами задач")
public class TypeController {

    private final TypeService typeService;

    /**
     * Получает список всех типов задач с возможностью сортировки по приоритету.
     *
     * @param ascending флаг сортировки по возрастанию (true - по возрастанию, false - по убыванию)
     * @return список типов задач
     */
    @Operation(summary = "Получить все типы",
            description = "Возвращает список всех типов задач с возможностью сортировки по приоритету",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TypeEntity> getAllTypes(
            @Parameter(description = "Сортировка по возрастанию приоритета", example = "true")
            @RequestParam(defaultValue = "true") boolean ascending) {
        return typeService.getAllTypes(ascending);
    }
}