package com.billing.testtask.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Сущность, описывающая задачу. Связана с одноименной таблицей
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "task")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class TaskEntity {
    /**
     * Идентификатор задачи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Наименование задачи
     */
    @Column(name = "name")
    private String name;

    /**
     * Описание задачи
     */
    @Column(name = "description")
    private String description;

    /**
     * Дата задачи
     */
    @Column(name = "task_date")
    private LocalDate taskDate;

    /**
     * Идентификатор тега задачи. Связана при помощи внешнего ключа с таблицей тегов
     */
    @ManyToOne
    @JoinColumn(name = "uid_tag", nullable = false, foreignKey = @ForeignKey(name = "fk_task_taguid"))
    private TagEntity tag;

    /**
     * Идентификатор типа задачи. Связана при помощи внешнего ключа с таблицей типов
     */
    @ManyToOne
    @JoinColumn(name = "uid_type", nullable = false, foreignKey = @ForeignKey(name = "fk_task_typeuid"))
    private TypeEntity type;
}
