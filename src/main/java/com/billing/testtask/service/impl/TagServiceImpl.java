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
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository repository;
    private final TaskRepository taskRepository;


    public TagServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }



    public TagEntity getTagsWithTasks() {
        return repository.findTagsWithTasks();
    }


    public TagWithTask getTagWithSortedTasks(Long tagId) {
        TagEntity tag = repository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + tagId));

        List<TaskEntity> tasks = taskRepository.findByTagIdOrderByTypePriority(tagId);

        return TagWithTask.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .tasks(tasks.stream()
                        .map(this::convertToTaskSimpleDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private GetTaskInfo convertToTaskSimpleDto(TaskEntity entity) {
        return GetTaskInfo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .taskDate(entity.getTaskDate())
                .typeTitle(entity.getType() != null ? entity.getType().getTitle() : "Без типа")

                .build();
    }



    @CachePut(cacheNames = "tagsCache", key = "#tag.id")
    @Override
    public TagModel save(TagModel tag) {
        TagEntity tagEntity = TagEntity.builder()
                .title(tag.getTitle())
                .tasks(new ArrayList<>())
                .build();

        if (tag.getId() != null) {
            Optional<TagEntity> optionalTagEntity = repository.findById(tag.getId());
            if (optionalTagEntity.isPresent()) {
                tagEntity.setId(tag.getId());
                tagEntity.setTitle(tag.getTitle());
                tagEntity.getTasks().addAll(optionalTagEntity.get().getTasks());
            }
        }
        repository.save(tagEntity);

        tag.setId(tagEntity.getId());
        List<TaskModel> tasks = new ArrayList<>();
        for (TaskEntity task : tagEntity.getTasks())
            tasks.add(TaskModel.builder()
                    .id(task.getId())
                    .name(task.getName())
                    .description(task.getDescription())
                    .taskDate(task.getTaskDate())
                    .tagId(tagEntity.getId())
                    .build());
        tag.setTasks(tasks);

        return tag;
    }

    @CacheEvict(cacheNames = "tagsCache", key = "#id")
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Cacheable(cacheNames = "tagsCache", key = "#id")
    @Override
    public TagModel getAllTaskByTag(Long id) {
        Optional<TagEntity> tagEntity = repository.findById(id);
        if (tagEntity.isPresent()) {
            List<TaskModel> tasks = new ArrayList<>();
            for (TaskEntity task : tagEntity.get().getTasks())
                tasks.add(TaskModel.builder()
                        .id(task.getId())
                        .name(task.getName())
                        .description(task.getDescription())
                        .taskDate(task.getTaskDate())
                        .tagId(tagEntity.get().getId())
                        .build());

            return TagModel.builder()
                    .id(tagEntity.get().getId())
                    .title(tagEntity.get().getTitle())
                    .tasks(tasks)
                    .build();
        } else
            throw new EntityNotFoundException("Tag[id=" + id + "] not found in database!");

    }
}
