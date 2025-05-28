package com.billing.testtask.service;

import com.billing.testtask.entity.TypeEntity;

import java.util.List;

public interface TypeService {
    List<TypeEntity> getAllTypes(boolean ascending);
}
