package com.billing.testtask.service;

import com.billing.testtask.dto.GetTaskInfo;
import com.billing.testtask.model.TaskModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

/**
 * Доменный сервис задач
 */
public interface TaskService {
    /**
     * Сохранения задачи
     * @param task модель данных задачи
     * @return идентификатор сохраненной задачи
     */
    Long save(TaskModel task);

    /**
     * Удаление задачи
     *
     * @param id идентификатор задачи
     */
    void delete(Long id);

    /**
     * Получения списка всех существующих задач
     *
     * @return список задач
     */
    List<TaskModel> getAllTasks();
    Page<TaskModel> getAllTasks(Pageable pageable);

    /**
     * Получение списка задач за заданную дату, отсортированных по приоритету (по убыванию)
     *
     * @param date дата для фильтрации задач
     * @return отсортированный список задач
     */
    List<GetTaskInfo> getTasksByDateSortedByPriority(LocalDate date);
}
