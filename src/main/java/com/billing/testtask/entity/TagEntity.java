package com.billing.testtask.entity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

/**
 * Сущность, описывающая тег. Связана с одноименной таблицей
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tag")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class TagEntity {
    /**
     * Идентификатор тега
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    /**
     * Заголовок тега
     */
    @Column(name = "title")
    private String title;

    /**
     * Связь с таблицей задач в отношении один ко многим
     */
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TaskEntity> tasks;
}
