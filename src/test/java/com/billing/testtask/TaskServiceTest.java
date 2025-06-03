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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для {@link TaskServiceImpl}.
 * Тестирует основные CRUD операции и дополнительные функции сервиса задач.
 * Использует тестовую базу данных с автоматической откаткой изменений после каждого теста.
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class TaskServiceTest {

    @Autowired
    private MockMvc mockMvc;

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

    /**
     * Инициализация тестовых данных перед каждым тестом.
     * <p>
     * Очищает базу данных и создает тестовые сущности:
     * - Тег (TagEntity)
     * - Тип задачи (TypeEntity)
     */
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

    /**
     * Тестирует сохранение задачи без указания типа.
     * <p>
     * Ожидается, что метод save() выбросит IllegalArgumentException
     * с сообщением "Тип задачи обязателен".
     */
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

    /**
     * Тестирует сохранение задачи с несуществующим типом.
     * <p>
     * Ожидается, что метод save() выбросит EntityNotFoundException.
     */
    @Test
    @DisplayName("Сохранение задачи с несуществующим типом - должно вызывать исключение")
    void save_ShouldThrowExceptionWhenTypeNotFound() {
        TaskModel model = createTestTaskModel("Task with invalid type");
        model.setTypeId(999L); // Несуществующий ID

        assertThrows(EntityNotFoundException.class,
                () -> taskService.save(model));
    }

    /**
     * Тестирует успешное сохранение задачи с корректными данными.
     * <p>
     * Проверяет:
     * - Возвращаемый ID не null
     * - Сохраненные данные соответствуют переданным
     * - Связи с тегом и типом установлены правильно
     */
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

    /**
     * Тестирует обновление существующей задачи.
     * <p>
     * Проверяет:
     * - Возвращаемый ID соответствует ID обновляемой задачи
     * - Данные задачи были обновлены
     */
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

    /**
     * Тестирует успешное удаление существующей задачи.
     * <p>
     * Проверяет, что задача удаляется из базы данных без ошибок.
     */
    @Test
    @DisplayName("Удаление существующей задачи - успешный сценарий")
    void delete_ShouldDeleteExistingTask() {
        TaskEntity task = createAndSaveTestTask("To be deleted");

        assertDoesNotThrow(() -> taskService.delete(task.getId()));
        assertFalse(taskRepository.existsById(task.getId()));
    }

    /**
     * Тестирует попытку удаления несуществующей задачи.
     * <p>
     * Ожидается, что метод delete() выбросит EntityNotFoundException.
     */
    @Test
    @DisplayName("Удаление несуществующей задачи - должно вызывать исключение")
    void delete_ShouldThrowExceptionWhenTaskNotFound() {
        assertThrows(EntityNotFoundException.class, () -> taskService.delete(999L));
    }

    /**
     * Тестирует получение списка всех задач.
     * <p>
     * Проверяет:
     * - Количество возвращаемых задач соответствует созданным
     * - Имена задач соответствуют ожидаемым
     */
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

    /**
     * Тестирует получение списка задач из пустой базы данных.
     * <p>
     * Ожидается, что метод вернет пустой список.
     */
    @Test
    @DisplayName("Получение всех задач из пустой БД - возвращает пустой список")
    void getAllTasks_ShouldReturnEmptyListForEmptyDB() {
        List<TaskModel> tasks = taskService.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    /**
     * Тестирует загрузку файла для задачи.
     * <p>
     * Проверяет успешную обработку запроса с файлом.
     * Замокана роль ADMIN.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Загрузка файла - успешный сценарий")
    void uploadFile_ShouldReturnSuccessMessage() throws Exception {
        // Подготовка тестового файла
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        // Выполнение запроса
        mockMvc.perform(multipart("https://localhost:8443/api/tasks/2/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    /**
     * Тестирует попытку загрузки без файла.
     * <p>
     * Ожидается ответ с кодом ошибки 500 (Internal Server Error).
     * Замокана роль ADMIN.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Загрузка файла - ошибка при отсутствии файла")
    void uploadFile_ShouldReturnErrorWhenNoFile() throws Exception {
        mockMvc.perform(multipart("https://localhost:8443/api/tasks/2/upload")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Создает тестовую модель задачи.
     *
     * @param name название задачи
     * @return созданная модель задачи
     */
    private TaskModel createTestTaskModel(String name) {
        return TaskModel.builder()
                .name(name)
                .description("Test description")
                .taskDate(LocalDate.now())
                .tagId(testTag.getId())
                .typeId(testType.getId())
                .build();
    }

    /**
     * Создает и сохраняет тестовую задачу в базе данных.
     *
     * @param name название задачи
     * @return сохраненная сущность задачи
     */
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