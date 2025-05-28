package com.billing.testtask.repository;

import com.billing.testtask.entity.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeRepository extends JpaRepository<TypeEntity, Long> {

    // Сортировка по возрастанию приоритета
    List<TypeEntity> findAllByOrderByLevelAsc();

    // Сортировка по убыванию приоритета
    List<TypeEntity> findAllByOrderByLevelDesc();

}