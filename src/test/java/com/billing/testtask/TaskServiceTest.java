package com.billing.testtask;

import com.billing.testtask.entity.TagEntity;
import com.billing.testtask.entity.TaskEntity;
import com.billing.testtask.entity.TypeEntity;
import com.billing.testtask.model.TaskModel;
import com.billing.testtask.repository.TagRepository;
import com.billing.testtask.repository.TaskRepository;
import com.billing.testtask.repository.TypeRepository;
import com.billing.testtask.service.impl.TaskServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TypeRepository typeRepository;

    private TagEntity testTag;
    private TypeEntity testType;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        taskRepository.deleteAll();
        tagRepository.deleteAll();
        typeRepository.deleteAll();

        // Создание тестовых данных
        testTag = tagRepository.save(
                TagEntity.builder()
                        .title("test-tag")
                        .build()
        );

        testType = typeRepository.save(
                TypeEntity.builder()
                        .title("test-type")
                        .level(1)
                        .build()
        );
    }

    @Test
    @DisplayName("Сохранение задачи без типа - должно вызывать исключение")
    void save_ShouldThrowExceptionWhenTypeIsNull() {
        TaskModel model = TaskModel.builder()
                .name("Invalid Task")
                .description("Description")
                .taskDate(LocalDate.now())
                .tagId(testTag.getId())
                // typeId не указан
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.save(model));

        assertEquals("Тип задачи обязателен", exception.getMessage());
    }

    @Test
    @DisplayName("Сохранение задачи с несуществующим типом - должно вызывать исключение")
    void save_ShouldThrowExceptionWhenTypeNotFound() {
        TaskModel model = createTestTaskModel("Task with invalid type");
        model.setTypeId(999L); // Несуществующий ID

        assertThrows(EntityNotFoundException.class,
                () -> taskService.save(model));
    }

    @Test
    @DisplayName("Сохранение задачи с корректными данными - успешный сценарий")
    void save_ShouldSuccessfullySaveTask() {
        TaskModel model = createTestTaskModel("Valid Task");

        Long taskId = taskService.save(model);
        assertNotNull(taskId);

        TaskEntity savedTask = taskRepository.findById(taskId).orElseThrow();
        assertEquals("Valid Task", savedTask.getName());
        assertEquals(testTag.getId(), savedTask.getTag().getId());
        assertEquals(testType.getId(), savedTask.getType().getId());
    }

    @Test
    @DisplayName("Обновление существующей задачи")
    void save_ShouldUpdateExistingTask() {
        // Создаем задачу для обновления
        TaskEntity existingTask = createAndSaveTestTask("Original name");

        // Подготавливаем модель для обновления
        TaskModel updateModel = TaskModel.builder()
                .id(existingTask.getId())
                .name("Updated name")
                .description("Updated description")
                .taskDate(LocalDate.now().plusDays(1))
                .tagId(testTag.getId())
                .typeId(testType.getId())
                .build();

        Long updatedId = taskService.save(updateModel);

        assertEquals(existingTask.getId(), updatedId);
        TaskEntity updatedTask = taskRepository.findById(updatedId).orElseThrow();
        assertEquals("Updated name", updatedTask.getName());
        assertEquals("Updated description", updatedTask.getDescription());
    }

    @Test
    @DisplayName("Удаление существующей задачи - успешный сценарий")
    void delete_ShouldDeleteExistingTask() {
        TaskEntity task = createAndSaveTestTask("To be deleted");

        assertDoesNotThrow(() -> taskService.delete(task.getId()));
        assertFalse(taskRepository.existsById(task.getId()));
    }

    @Test
    @DisplayName("Удаление несуществующей задачи - должно вызывать исключение")
    void delete_ShouldThrowExceptionWhenTaskNotFound() {
        assertThrows(EntityNotFoundException.class, () -> taskService.delete(999L));
    }

    @Test
    @DisplayName("Получение всех задач - возвращает корректный список")
    void getAllTasks_ShouldReturnCorrectList() {
        // Создаем 5 тестовых задач
        for (int i = 1; i <= 5; i++) {
            createAndSaveTestTask("Task " + i);
        }

        List<TaskModel> tasks = taskService.getAllTasks();

        assertEquals(5, tasks.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("Task " + (i + 1), tasks.get(i).getName());
        }
    }

    @Test
    @DisplayName("Получение всех задач из пустой БД - возвращает пустой список")
    void getAllTasks_ShouldReturnEmptyListForEmptyDB() {
        List<TaskModel> tasks = taskService.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    // Вспомогательные методы
    private TaskModel createTestTaskModel(String name) {
        return TaskModel.builder()
                .name(name)
                .description("Test description")
                .taskDate(LocalDate.now())
                .tagId(testTag.getId())
                .typeId(testType.getId())
                .build();
    }

    private TaskEntity createAndSaveTestTask(String name) {
        return taskRepository.save(
                TaskEntity.builder()
                        .name(name)
                        .description("Description")
                        .taskDate(LocalDate.now())
                        .tag(testTag)
                        .type(testType)
                        .build()
        );
    }
}