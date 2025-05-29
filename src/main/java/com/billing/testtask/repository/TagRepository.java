package com.billing.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.billing.testtask.entity.TagEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Query("SELECT DISTINCT t FROM TagEntity t JOIN FETCH t.tasks WHERE t.tasks IS NOT EMPTY")
    List<TagEntity> findTagsWithTasks();
}