package com.billing.testtask.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.billing.testtask.dto.GetTaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.billing.testtask.model.TaskModel;
import com.billing.testtask.service.impl.TaskServiceImpl;

import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * REST контроллер для управления задачами.
 * Предоставляет CRUD операции и дополнительные методы для работы с задачами.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task API", description = "Операции с задачами")
public class TaskController {
    @Autowired
    private TaskServiceImpl taskService;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Получает список всех задач.
     *
     * @return ответ-сущность всех задач
     */
    @Operation(summary = "Получить все задачи",
            description = "Возвращает список всех задач",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskModel>> getAllTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    /**
     * Получает страницу с задачами.
     *
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с задачами
     */
    @Operation(summary = "Пагинация для метода получения всех задач",
            description = "Возвращает список всех задач",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping("/pagination")
    public ResponseEntity<Page<TaskModel>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getAllTasks(PageRequest.of(page, size)));
    }

    /**
     * Создает или редактирует задачу.
     *
     * @param taskModel данные задачи
     * @param bindingResult результат валидации
     * @return созданная задача
     */
    @Operation(summary = "Создать или обновить задачу",
            description = "Создает новую задачу или обновляет существующую. Для создания новой задачи id должен отсутствовать, " +
                    "для обновления - должен присутствовать.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача успешно создана/обновлена"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера.")
            })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTask(
            @RequestBody @Valid TaskModel taskModel,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(bindingResult.getAllErrors().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        else
            return new ResponseEntity<>(taskService.save(taskModel), HttpStatus.CREATED);
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return сообщение об успешном удалении
     */
    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id) {
        taskService.delete(id);
        return new ResponseEntity<>("Задача с  ID " + id + " была удалена", HttpStatus.OK);
    }

    /**
     * Получает задачи за указанную дату, отсортированные по приоритету.
     *
     * @param date дата для фильтрации
     * @return список задач с сортировкой по приоритету
     */
    @Operation(summary = "Получить задачи по дате",
            description = "Возвращает задачи за указанную дату, отсортированные по приоритету",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping("/by-date")
    public ResponseEntity<List<GetTaskInfo>> getTasksByDateSortedByPriority(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GetTaskInfo> tasks = taskService.getTasksByDateSortedByPriority(date);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Загружает файл для задачи.
     *
     * @param id идентификатор задачи
     * @param file загружаемый файл
     * @return сообщение о результате загрузки
     * @throws IOException при ошибках работы с файлом
     */
    @Operation(summary = "Загрузить файл для задачи",
            description = "Загружает файл-вложение для указанной задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
                    @ApiResponse(responseCode = "500", description = "Файл не предоставлен.Ошибка сервера")
            })
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdir();

            String filename = "task-" + id + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadDir.getAbsolutePath() + "/" + filename));
            return new ResponseEntity<>("File " + filename + " was upload!", HttpStatus.OK);
        } else
            return new ResponseEntity<>("File for upload not found!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
