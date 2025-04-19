package ru.t1.school.dto;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) для представления статуса задачи.
 * <p>
 * Этот класс используется для передачи данных о статусе задачи между клиентом и сервером.
 * Он включает в себя такую информацию о задаче, как идентификатор задачи, статус и описание статуса.
 * </p>
 */
public class TaskStatusDTO {
    private Long taskId;
    private String status;
    private String description;

    public TaskStatusDTO() {
    }

    public TaskStatusDTO(Long taskId, String status, String description) {
        this.taskId = taskId;
        this.status = status;
        this.description = description;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TaskStatusDTO{" +
                "taskId=" + taskId +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStatusDTO that = (TaskStatusDTO) o;
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, status, description);
    }
}