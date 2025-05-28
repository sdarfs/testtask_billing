package com.billing.testtask.service.impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.billing.testtask.dto.GetTaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.billing.testtask.entity.TagEntity;
import com.billing.testtask.entity.TaskEntity;
import com.billing.testtask.model.TaskModel;
import com.billing.testtask.repository.TaskRepository;
import com.billing.testtask.service.TaskService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository repository;

    /**
     * Сохраняет задачу в репозитории.
     *
     * Этот метод принимает объект модели задачи, преобразует его в сущность задачи
     * и сохраняет в базе данных. Если задача уже существует, она будет обновлена.
     *
     * @param task объект модели задачи, который содержит данные для сохранения.
     * @return идентификатор сохраненной задачи (ID).
     */
    @Override
    public Long save(TaskModel task) {
        TaskEntity entity = TaskEntity.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .taskDate(task.getTaskDate())
                .tag(TagEntity.builder()
                        .id(task.getTagId())
                        .build())
                .build();

        return repository.save(entity).getId();
    }

    /**
     * Удаляет задачу из базы данных по указанному идентификатору.
     * Этот метод вызывает репозиторий для удаления задачи с заданным идентификатором.
     * @param id идентификатор задачи, которую необходимо удалить
     */
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }


    public Page<TaskModel> getAllTasks(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToTaskModel);
    }

    private TaskModel convertToTaskModel(TaskEntity entity) {
        return TaskModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .taskDate(entity.getTaskDate())
                .tagId(entity.getTag() != null ? entity.getTag().getId() : null)
                .build();
    }



    @Override
    public List<TaskModel> getAllTasks() {
        List<TaskEntity> entityList = repository.findAll();

        List<TaskModel> tasks = new ArrayList<>();
        for (TaskEntity task : entityList)
            tasks.add(TaskModel.builder()
                    .id(task.getId())
                    .name(task.getName())
                    .description(task.getDescription())
                    .taskDate(task.getTaskDate())
                    .tagId(task.getTag().getId())
                    .build());
        return tasks;
    }

    @Override
    public List<GetTaskInfo> getTasksByDateSortedByPriority(LocalDate date) {
        return repository.findByTaskDateOrderByTypePriority(date).stream()
                .map(entity -> GetTaskInfo.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .taskDate(entity.getTaskDate())
                        .typeTitle(entity.getType() != null ? entity.getType().getTitle() : null)
                        .build())
                .collect(Collectors.toList());
    }

}
