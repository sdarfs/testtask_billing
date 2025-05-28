package com.billing.testtask.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.billing.testtask.entity.TaskEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("SELECT t FROM TaskEntity t JOIN FETCH t.type WHERE t.taskDate = :date ORDER BY t.type.level DESC")
    List<TaskEntity> findByTaskDateOrderByTypePriority(@Param("date") LocalDate date);

    @Query("SELECT t FROM TaskEntity t JOIN FETCH t.type WHERE t.tag.id = :tagId ORDER BY t.type.level DESC")
    List<TaskEntity> findByTagIdOrderByTypePriority(@Param("tagId") Long tagId);

    Page<TaskEntity> findAll(Pageable pageable);
}