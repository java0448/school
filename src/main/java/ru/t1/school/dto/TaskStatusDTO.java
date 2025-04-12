package ru.t1.school.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) для представления статуса задачи.
 * <p>
 * Этот класс используется для передачи данных о статусе задачи между клиентом и сервером.
 * Он включает в себя такую информацию о задаче, как идентификатор задачи, статус и описание статуса.
 * </p>
 */

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