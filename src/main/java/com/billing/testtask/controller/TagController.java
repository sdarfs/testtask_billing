package com.billing.testtask.controller;

import com.billing.testtask.dto.TagWithTask;
import com.billing.testtask.entity.TagEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.billing.testtask.model.TagModel;
import com.billing.testtask.service.impl.TagServiceImpl;

import jakarta.validation.Valid;


/**
 * Контроллер с crud операциями по тегам
 */
@RestController
@Tag(name = "Tag API", description = "Операции с тегами")
public class TagController {
    @Autowired
    private TagServiceImpl tagService;

    /**
     * POST-запрос на создание нового тега
     * POST-запрос на изменение существующего тега
     * @param tagModel модель с данными тега
     * @param bindingResult ошибки валидации
     * @return ответ-сущность со статусом обработки и соответствующим сообщением
     */
    @Operation(summary = "Создать или обновить тег",
            description = "Создает новый тег или обновляет существующий",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Тег успешно создан/обновлен"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    @PostMapping("/tag")
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
     * GET-запрос на вывод данных тега по идентификатору
     * @param id идентификатор тега
     * @return ответ-сущность со статусом обработки и соответствующим сообщением
     */
    @Operation(summary = "Получить тег по ID",
            description = "Возвращает тег со списком связанных задач",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Тег найден"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            })
    @GetMapping("/tag/{id}")
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
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id) {
        tagService.delete(id);
        return new ResponseEntity<>("Tag was successful deleted!", HttpStatus.OK);
    }







    @Operation(summary = "Получить тег с задачами (отсортированными)",
            description = "Возвращает тег с задачами, отсортированными по приоритету",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "404", description = "Тег не найден")
            })
    // получение тега по идентификатору с задачи отсортированными по приоритету
    @GetMapping("/{id}/with-tasks")
    public ResponseEntity<TagWithTask> getTagWithTasks(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagWithSortedTasks(id));
    }


    @Operation(summary = "Получить теги с задачами",
            description = "Возвращает теги, у которых есть хотя бы одна задача",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    // Получить теги, у которых есть задачи
    @GetMapping("/with-tasks")
    public ResponseEntity<TagEntity> getTagsWithTasks() {
        return ResponseEntity.ok(tagService.getTagsWithTasks());
    }

}
