package com.billing.testtask.service.impl;

import com.billing.testtask.dto.GetTaskInfo;
import com.billing.testtask.entity.TagEntity;
import com.billing.testtask.entity.TaskEntity;
import com.billing.testtask.model.TaskModel;
import com.billing.testtask.repository.TaskRepository;
import com.billing.testtask.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с задачами.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Long save(TaskModel task) {
        if (task == null) {
            throw new IllegalArgumentException("Task model cannot be null");
        }

        TaskEntity entity = convertToEntity(task);
        TaskEntity savedEntity = taskRepository.save(entity);
        return savedEntity.getId();
    }

    @Override
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskModel> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToTaskModel)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TaskModel> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(this::convertToTaskModel);
    }

    @Override
    public List<GetTaskInfo> getTasksByDateSortedByPriority(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date parameter cannot be null");
        }

        return taskRepository.findByTaskDateOrderByTypePriority(date).stream()
                .map(this::convertToGetTaskInfo)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует модель задачи в сущность.
     *
     * @param model модель задачи
     * @return сущность задачи
     */
    private TaskEntity convertToEntity(TaskModel model) {
        return TaskEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .taskDate(model.getTaskDate())
                .tag(model.getTagId() != null ?
                        TagEntity.builder().id(model.getTagId()).build() : null)
                .build();
    }

    /**
     * Преобразует сущность задачи в модель.
     *
     * @param entity сущность задачи
     * @return модель задачи
     */
    private TaskModel convertToTaskModel(TaskEntity entity) {
        return TaskModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .taskDate(entity.getTaskDate())
                .tagId(entity.getTag() != null ? entity.getTag().getId() : null)
                .typeId(entity.getType() != null ? entity.getType().getId() : null)
                .build();
    }

    /**
     * Преобразует сущность задачи в DTO с дополнительной информацией.
     *
     * @param entity сущность задачи
     * @return DTO с информацией о задаче
     */
    private GetTaskInfo convertToGetTaskInfo(TaskEntity entity) {
        return GetTaskInfo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .taskDate(entity.getTaskDate())
                .typeTitle(entity.getType() != null ? entity.getType().getTitle() : "No type")
                .build();
    }
}