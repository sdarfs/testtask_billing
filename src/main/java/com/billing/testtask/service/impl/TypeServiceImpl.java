package com.billing.testtask.service.impl;

import com.billing.testtask.entity.TypeEntity;
import com.billing.testtask.repository.TypeRepository;
import com.billing.testtask.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для работы с типами задач.
 * Содержит логику получения типов задач из репозитория и кэширования результатов.
 */
@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    /**
     * Получить все типы задач с возможностью сортировки по приоритету.
     *
     * @param ascending указывает, следует ли сортировать типы по возрастанию приоритета.
     *                  Если true, типы будут отсортированы по возрастанию приоритета.
     *                  Если false, типы будут отсортированы по убыванию приоритета.
     * @return список всех типов задач, отсортированных в соответствии с параметром ascending.
     */
    @Cacheable(cacheNames = "typesCache", key = "{#root.methodName, #ascending}")
    @Override
    public List<TypeEntity> getAllTypes(boolean ascending) {
        return ascending ?
                typeRepository.findAllByOrderByLevelDesc() :
                typeRepository.findAllByOrderByLevelAsc();
    }
}
