package com.billing.testtask.service;

import com.billing.testtask.entity.TypeEntity;

import java.util.List;

/**
 * Интерфейс сервиса для работы с типами задач.
 * Предоставляет методы для получения типов задач.
 */
public interface TypeService {

    /**
     * Получить все типы задач.
     *
     * @param ascending указывает, следует ли сортировать типы по возрастанию приоритета.
     *                  Если true, типы будут отсортированы по возрастанию приоритета.
     *                  Если false, типы будут отсортированы по убыванию приоритета.
     * @return список всех типов задач, отсортированных в соответствии с параметром ascending.
     */
    List<TypeEntity> getAllTypes(boolean ascending);
}
