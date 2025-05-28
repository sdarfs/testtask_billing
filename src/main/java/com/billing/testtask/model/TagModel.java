package com.billing.testtask.model;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель данных тега для работы со стороны клиента
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TagModel {
    private Long id = null;

    @NotBlank
    @Size(max = 255)
    private String title;

    private List<TaskModel> tasks = new ArrayList<>();
}
