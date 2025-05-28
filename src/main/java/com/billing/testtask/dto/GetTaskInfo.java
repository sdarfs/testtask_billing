package com.billing.testtask.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GetTaskInfo {
    private Long id;
    private String name;
    private String description;
    private LocalDate taskDate;
    private String typeTitle;
}