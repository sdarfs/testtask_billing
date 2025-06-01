package com.billing.testtask.service.impl;

import com.billing.testtask.dto.GetTaskInfo;
import com.billing.testtask.entity.TagEntity;
import com.billing.testtask.entity.TaskEntity;
import com.billing.testtask.entity.TypeEntity;
import com.billing.testtask.model.TaskModel;
import com.billing.testtask.repository.TagRepository;
import com.billing.testtask.repository.TaskRepository;
import com.billing.testtask.repository.TypeRepository;
import com.billing.testtask.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с задачами.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final TypeRepository typeRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TagRepository tagRepository, TypeRepository typeRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.typeRepository = typeRepository;
    }


    @Override
    public Long save(TaskModel task) {
        // Валидация входных данных
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть пустой");
        }
        if (task.getTagId() == null) {
            throw new IllegalArgumentException("Тег ID обязателен");
        }
        if (task.getTypeId() == null) {
            throw new IllegalArgumentException("Тип задачи обязателен");
        }

        // Получение связанных сущностей
        TagEntity tag = tagRepository.findById(task.getTagId())
                .orElseThrow(() -> new EntityNotFoundException("Тег не найден"));

        TypeEntity type = typeRepository.findById(task.getTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Тип не найден"));

        // Создание/обновление задачи
        TaskEntity entity;
        if (task.getId() != null) {
            entity = taskRepository.findById(task.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Задача " + task.getId() + " не найдена"));
            entity.setName(task.getName());
            entity.setDescription(task.getDescription());
            entity.setTaskDate(task.getTaskDate());
        } else {
            entity = TaskEntity.builder()
                    .name(task.getName())
                    .description(task.getDescription())
                    .taskDate(task.getTaskDate())
                    .build();
        }

        entity.setTag(tag);
        entity.setType(type);

        return taskRepository.save(entity).getId();
    }

    @Override
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Задача с id: " + id + " не найдена.");
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
            throw new IllegalArgumentException("Дата не может быть пустой");
        }

        return taskRepository.findByTaskDateOrderByTypePriority(date).stream()
                .map(this::convertToGetTaskInfo)
                .collect(Collectors.toList());
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
                .typeTitle(entity.getType() != null ? entity.getType().getTitle() : "Нет типа")
                .build();
    }
}