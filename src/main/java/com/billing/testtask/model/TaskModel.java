package com.billing.testtask.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Модель данных задачи для работы со стороны клиента
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskModel {

    private Long id = null;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate taskDate;

    private Long tagId;
    private Long typeId;

    @JsonIgnore
    @Getter
    private Integer typePriority;
    @JsonIgnore
    @Getter
    private String namePriority;


}
