package ru.t1.school.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TaskStatusDTO {
    private Long taskId;
    private String status;
    private String description;
}