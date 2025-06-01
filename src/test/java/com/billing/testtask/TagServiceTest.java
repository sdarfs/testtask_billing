package com.billing.testtask;

import com.billing.testtask.controller.TagController;
import com.billing.testtask.dto.TagWithTask;
import com.billing.testtask.entity.TagEntity;
import com.billing.testtask.model.TagModel;
import com.billing.testtask.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link TagController}.
 * Проверяет корректность работы REST API для управления тегами.
 */
class TagServiceTest {

    @Mock
    private TagServiceImpl tagService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private TagController tagController;

    private TagModel testTagModel;
    private TagEntity testTagEntity;
    private TagWithTask testTagWithTask;

    /**
     * Инициализация тестовых данных перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testTagModel = new TagModel();
        testTagModel.setId(1L);
        testTagModel.setTitle("Test Tag");

        testTagEntity = new TagEntity();
        testTagEntity.setId(1L);
        testTagEntity.setTitle("Test Tag");

        testTagWithTask = TagWithTask.builder()
                .id(1L)
                .title("Test Tag")
                .tasks(Collections.emptyList())
                .build();
    }

    /**
     * Тестирование успешного создания тега.
     * Проверяет:
     * - Код ответа 201 (CREATED)
     * - Корректность возвращаемого тела ответа
     * - Вызов сервисного метода save()
     */
    @Test
    void createTag_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(tagService.save(any(TagModel.class))).thenReturn(testTagModel);

        ResponseEntity<?> response = tagController.createTag(testTagModel, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTagModel, response.getBody());
        verify(tagService, times(1)).save(testTagModel);
    }

    /**
     * Тестирование создания тега с ошибками валидации.
     * Проверяет:
     * - Код ответа 500 (INTERNAL_SERVER_ERROR)
     * - Отсутствие вызова сервисного метода save()
     */
    @Test
    void createTag_ValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = tagController.createTag(testTagModel, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(tagService, never()).save(any());
    }

    /**
     * Тестирование успешного получения тега по ID.
     * Проверяет:
     * - Код ответа 200 (OK)
     * - Корректность возвращаемого тела ответа
     * - Вызов сервисного метода getAllTaskByTag()
     */
    @Test
    void getAllTasks_Success() {
        when(tagService.getAllTaskByTag(anyLong())).thenReturn(testTagModel);

        ResponseEntity<TagModel> response = tagController.getAllTasks(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTagModel, response.getBody());
        verify(tagService, times(1)).getAllTaskByTag(1L);
    }

    /**
     * Тестирование успешного удаления тега.
     * Проверяет:
     * - Код ответа 200 (OK)
     * - Корректность сообщения об успешном удалении
     * - Вызов сервисного метода delete()
     */
    @Test
    void deleteTag_Success() {
        doNothing().when(tagService).delete(anyLong());

        ResponseEntity<String> response = tagController.deleteTag(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tag was successful deleted!", response.getBody());
        verify(tagService, times(1)).delete(1L);
    }

    /**
     * Тестирование успешного получения тега с отсортированными задачами.
     * Проверяет:
     * - Код ответа 200 (OK)
     * - Корректность возвращаемого тела ответа
     * - Вызов сервисного метода getTagWithSortedTasks()
     */
    @Test
    void getTagWithTasks_Success() {
        when(tagService.getTagWithSortedTasks(anyLong())).thenReturn(testTagWithTask);

        ResponseEntity<TagWithTask> response = tagController.getTagWithTasks(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTagWithTask, response.getBody());
        verify(tagService, times(1)).getTagWithSortedTasks(1L);
    }

    /**
     * Тестирование успешного получения списка тегов с задачами.
     * Проверяет:
     * - Код ответа 200 (OK)
     * - Корректность возвращаемого списка
     * - Вызов сервисного метода getTagsWithTasks()
     */
    @Test
    void getTagsWithTasks_Success() {
        List<TagEntity> tags = Collections.singletonList(testTagEntity);
        when(tagService.getTagsWithTasks()).thenReturn(tags);

        ResponseEntity<List<TagEntity>> response = tagController.getTagsWithTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testTagEntity, response.getBody().get(0));
        verify(tagService, times(1)).getTagsWithTasks();
    }
}