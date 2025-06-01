package com.billing.testtask.controller;

import com.billing.testtask.dto.TagWithTask;
import com.billing.testtask.entity.TagEntity;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.billing.testtask.model.TagModel;
import com.billing.testtask.service.impl.TagServiceImpl;

import jakarta.validation.Valid;

import java.util.List;


/**
 * REST контроллер для управления тегами задач.
 * Предоставляет CRUD операции и дополнительные методы для работы с тегами.
 */
@OpenAPIDefinition(
        info = @Info(title = "Spring OpenAPI example", version = "1.0.0"))
@RestController
@RequestMapping("/api/tags")
@Tag(name = "Tag API", description = "Операции с тегами")
public class TagController {
    @Autowired
    private TagServiceImpl tagService;

    /**
     * Создает новый тег или обновляет существующий.
     *
     * @param tagModel DTO с данными тега
     * @param bindingResult результат валидации
     * @return созданный/обновленный тег
     */
    @Operation(summary = "Создать или обновить тег",
            description = "Создает новый тег или обновляет существующий",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Тег успешно создан/обновлен",
                            content = @Content(schema = @Schema(implementation = TagModel.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера.",
                            content = @Content)
            })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTag(
            @RequestBody @Valid TagModel tagModel,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(bindingResult.getAllErrors().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(tagService.save(tagModel), HttpStatus.CREATED);
    }

    /**
     * Получает тег по его идентификатору.
     * @param id идентификатор тега
     * @return ответ-сущность со статусом обработки и соответствующим сообщением
     */
    @Operation(summary = "Получить тег по ID",
            description = "Возвращает тег со списком связанных задач",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Тег найден"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            })
    @GetMapping("/{id}")
    public ResponseEntity<TagModel> getAllTasks(@PathVariable("id") Long id) {
        return new ResponseEntity<>(tagService.getAllTaskByTag(id), HttpStatus.OK);
    }

    /**
     * DELETE-запрос на удаление тега из БД по идентификатору вместе с его задачами
     * @param id идентификатор тега
     * @return ответ-сущность со статусом обработки и соответствующим сообщением
     */
    @Operation(summary = "Удалить тег",
            description = "Удаляет тег по ID вместе со связанными задачами",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Тег успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            })
    @DeleteMapping("/tag/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable("id") Long id) {
        tagService.delete(id);
        return new ResponseEntity<>("Tag was successful deleted!", HttpStatus.OK);
    }

    /**
     * Получает тег с отсортированными по приоритету задачами.
     *
     * @param id идентификатор тега
     * @return тег с отсортированными задачами
     */
    @Operation(summary = "Получить тег с задачами (отсортированными)",
            description = "Возвращает тег с задачами, отсортированными по приоритету",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            })
    @GetMapping("/{id}/with-tasks")
    public ResponseEntity<TagWithTask> getTagWithTasks(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagWithSortedTasks(id));
    }


    /**
     * Получает список всех тегов, у которых есть задачи.
     *
     * @return список тегов с задачами
     */
    @Operation(summary = "Получить все теги с задачами",
            description = "Возвращает список тегов, у которых есть хотя бы одна задача",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос")
            })
    @GetMapping("/with-tasks")
    public ResponseEntity<List<TagEntity>> getTagsWithTasks() {
        return ResponseEntity.ok(tagService.getTagsWithTasks());
    }

}
