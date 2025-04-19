package ru.t1.school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) для представления задачи.
 * <p>
 * Этот класс используется для передачи данных о задаче между клиентом и сервером.
 * Он включает в себя такую информацию о задаче, как идентификатор, заголовок, описание,
 * идентификатор пользователя и статус задачи.
 * </p>
 */
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String status = "NEW"; // Значение по умолчанию

    public TaskDTO() {
    }

    public TaskDTO(Long id, String title, String description, Long userId, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDTO taskDTO = (TaskDTO) o;
        return Objects.equals(id, taskDTO.id) &&
                Objects.equals(title, taskDTO.title) &&
                Objects.equals(description, taskDTO.description) &&
                Objects.equals(userId, taskDTO.userId) &&
                Objects.equals(status, taskDTO.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, userId, status);
    }
}