package com.billing.testtask.service.impl;

import com.billing.testtask.entity.TypeEntity;
import com.billing.testtask.repository.TypeRepository;
import com.billing.testtask.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Cacheable(cacheNames = "typesCache", key = "{#root.methodName, #ascending}")
    @Override
    public List<TypeEntity> getAllTypes(boolean ascending) {
        return ascending ?
                typeRepository.findAllByOrderByLevelAsc() :
                typeRepository.findAllByOrderByLevelDesc();
    }
}