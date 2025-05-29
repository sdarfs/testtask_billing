package com.billing.testtask.service;

import com.billing.testtask.dto.GetTaskInfo;
import com.billing.testtask.model.TaskModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для управления задачами.
 * Предоставляет CRUD-операции и дополнительные методы для работы с задачами.
 */
public interface TaskService {

    /**
     * Сохраняет или обновляет задачу.
     *
     * @param task модель задачи для сохранения
     * @return идентификатор сохраненной задачи
     * @throws IllegalArgumentException если переданная модель задачи некорректна
     */
    Long save(TaskModel task);

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи для удаления
     * @throws jakarta.persistence.EntityNotFoundException если задача с указанным ID не найдена
     */
    void delete(Long id);

    /**
     * Получает список всех задач.
     *
     * @return список всех задач в виде моделей
     */
    List<TaskModel> getAllTasks();

    /**
     * Получает страницу с задачами с поддержкой пагинации.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с задачами
     */
    Page<TaskModel> getAllTasks(Pageable pageable);

    /**
     * Получает задачи за указанную дату, отсортированные по приоритету типа задачи.
     *
     * @param date дата для фильтрации задач
     * @return список задач с дополнительной информацией, отсортированный по приоритету (по убыванию)
     * @throws IllegalArgumentException если дата не указана
     */
    List<GetTaskInfo> getTasksByDateSortedByPriority(LocalDate date);
}