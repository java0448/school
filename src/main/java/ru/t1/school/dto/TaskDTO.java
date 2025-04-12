package ru.t1.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) для представления задачи.
 * <p>
 * Этот класс используется для передачи данных о задаче между клиентом и сервером.
 * Он включает в себя такую информацию о задаче, как идентификатор, заголовок, описание,
 * идентификатор пользователя и статус задачи.
 * </p>
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String status = "NEW"; // Значение по умолчанию
}