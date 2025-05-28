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
 * CRUD операции по задачам
 */
@RestController
@Tag(name = "Task API", description = "Операции с задачами")
public class TaskController {
    @Autowired
    private TaskServiceImpl taskService;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * GET-запрос на получение данных обо всех существующих задачах
     * @return ответ-сущность со статусом обработки и сообщением
     */
    @Operation(summary = "Получить все задачи",
            description = "Возвращает список всех задач",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskModel>> getAllTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }
    @Operation(summary = "Пагинация для метода получения всех задач",
            description = "Возвращает список всех задач",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping("/tasks/pagination")
    public Page<TaskModel> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return taskService.getAllTasks(PageRequest.of(page, size));
    }

    /**
     * POST-запрос на создание новой задачи
     * POST-запрос на редактирование новой задачи
     * @param taskModel     модель данных задачи
     * @param bindingResult ошибки валидации
     * @return ответ-сущность со статусом обработки и сообщением
     */
    @Operation(summary = "Создать или обновить задачу",
            description = "Создает новую задачу или обновляет существующую",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача успешно создана/обновлена"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
            })
    @PostMapping("/task")
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
     * DELETE-запрос удаления задачи по идентификатору
     * @param id идентификатор задачи
     * @return ответ-сущность со статусом обработки и id удаленной задачи + соответствующее сообщение
     */
    @Operation(summary = "Удалить задачу",
            description = "Удаляет задачу по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id) {
        taskService.delete(id);
        return new ResponseEntity<>("Task with ID " + id + " was successfully deleted", HttpStatus.OK);
    }

    /**
     * GET-запрос на получение задач за определенную дату, отсортированных по приоритету
     * @param date дата в формате yyyy-MM-dd
     * @return список задач с сортировкой по приоритету (по убыванию)
     */
    @Operation(summary = "Получить задачи по дате",
            description = "Возвращает задачи за указанную дату, отсортированные по приоритету",
            responses = @ApiResponse(responseCode = "200", description = "Успешный запрос"))
    @GetMapping("/tasks/by-date")
    public ResponseEntity<List<GetTaskInfo>> getTasksByDateSortedByPriority(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GetTaskInfo> tasks = taskService.getTasksByDateSortedByPriority(date);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }




    /**
     * POST-запрос: загрузка файла-вложения для задачи
     *
     * @param id   идентификатор задачи
     * @param file загружаемый файл
     * @return ответ-сущность со статусом обработки и соответствующим сообщением
     * @throws IOException
     */
    @Operation(summary = "Загрузить файл для задачи",
            description = "Загружает файл-вложение для указанной задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
                    @ApiResponse(responseCode = "400", description = "Файл не предоставлен")
            })
    @PostMapping("/task/{id}/upload")
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
