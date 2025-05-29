package com.billing.testtask.service.impl;

import com.billing.testtask.dto.GetTaskInfo;
import com.billing.testtask.dto.TagWithTask;
import com.billing.testtask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.billing.testtask.entity.TagEntity;
import com.billing.testtask.entity.TaskEntity;
import com.billing.testtask.model.TagModel;
import com.billing.testtask.model.TaskModel;
import com.billing.testtask.repository.TagRepository;
import com.billing.testtask.service.TagService;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с тегами и связанными задачами
 */
@Service
public class TagServiceImpl implements TagService {
    private final TagRepository repository;
    private final TaskRepository taskRepository;

    @Autowired
    public TagServiceImpl(TagRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    /**
     * Получает все теги с связанными задачами
     *
     * @return сущность TagEntity с тегами и задачами
     */
    public List<TagEntity> getTagsWithTasks() {
        return repository.findTagsWithTasks();
    }

    /**
     * Получает тег с отсортированными по приоритету типа задачами
     *
     * @param tagId идентификатор тега
     * @return DTO с информацией о теге и отсортированными задачами
     * @throws EntityNotFoundException если тег не найден
     */
    public TagWithTask getTagWithSortedTasks(Long tagId) {
        TagEntity tag = repository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Тег с id=" + tagId + " не найден"));

        List<TaskEntity> tasks = taskRepository.findByTagIdOrderByTypePriority(tagId);

        return TagWithTask.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .tasks(tasks.stream()
                        .map(this::convertToTaskSimpleDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Конвертирует сущность задачи в DTO с основной информацией
     *
     * @param entity сущность задачи
     * @return DTO с информацией о задаче
     */
    private GetTaskInfo convertToTaskSimpleDto(TaskEntity entity) {
        return GetTaskInfo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .taskDate(entity.getTaskDate())
                .typeTitle(entity.getType() != null ? entity.getType().getTitle() : "Без типа")
                .build();
    }

    /**
     * Сохраняет или обновляет тег
     *
     * @param tag модель тега для сохранения
     * @return сохраненная модель тега с обновленным ID и задачами
     */
    @CachePut(cacheNames = "tagsCache", key = "#tag.id")
    @Override
    public TagModel save(TagModel tag) {
        TagEntity tagEntity = TagEntity.builder()
                .title(tag.getTitle())
                .tasks(new ArrayList<>())
                .build();

        if (tag.getId() != null) {
            repository.findById(tag.getId()).ifPresent(existingTag -> {
                tagEntity.setId(tag.getId());
                tagEntity.setTitle(tag.getTitle());
                tagEntity.getTasks().addAll(existingTag.getTasks());
            });
        }

        repository.save(tagEntity);
        tag.setId(tagEntity.getId());

        List<TaskModel> tasks = tagEntity.getTasks().stream()
                .map(task -> TaskModel.builder()
                        .id(task.getId())
                        .name(task.getName())
                        .description(task.getDescription())
                        .taskDate(task.getTaskDate())
                        .tagId(tagEntity.getId())
                        .build())
                .collect(Collectors.toList());

        tag.setTasks(tasks);
        return tag;
    }

    /**
     * Удаляет тег по идентификатору
     *
     * @param id идентификатор тега для удаления
     */
    @CacheEvict(cacheNames = "tagsCache", key = "#id")
    @Override
    public void delete(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тег[id=" + id + "] не найден в базе данных"));
        repository.deleteById(id);
    }

    /**
     * Получает тег со всеми связанными задачами
     *
     * @param id идентификатор тега
     * @return модель тега с задачами
     * @throws EntityNotFoundException если тег не найден
     */
    @Cacheable(cacheNames = "tagsCache", key = "#id")
    @Override
    public TagModel getAllTaskByTag(Long id) {
        TagEntity tagEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тег[id=" + id + "] не найден в базе данных"));

        List<TaskModel> tasks = tagEntity.getTasks().stream()
                .map(task -> TaskModel.builder()
                        .id(task.getId())
                        .name(task.getName())
                        .description(task.getDescription())
                        .taskDate(task.getTaskDate())
                        .tagId(tagEntity.getId())
                        .typeId(task.getType().getId())
                        .build())
                .collect(Collectors.toList());

        return TagModel.builder()
                .id(tagEntity.getId())
                .title(tagEntity.getTitle())
                .tasks(tasks)
                .build();
    }
}