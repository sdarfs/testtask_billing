package com.billing.testtask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

/**
 * Сущность, описывающая тип. Связана с одноименной таблицей
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "type")
public class TypeEntity {
    /**
     * Идентификатор тега
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Заголовок типа
     */
    @Column(name = "title")
    private String title;

    /**
     * Приоритет типа
     */
    @Column(name = "level")
    private Integer level;

    /**
     * Связь с таблицей задач в отношении один ко многим
     */
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TaskEntity> tasks;
}
