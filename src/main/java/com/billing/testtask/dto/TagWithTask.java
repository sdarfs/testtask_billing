package com.billing.testtask.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagWithTask {
    private Long id;
    private String title;
    private List<GetTaskInfo> tasks;
}
