package ru.t1.school.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.t1.school.dto.TaskDTO;
import ru.t1.school.entity.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TaskMapperTest {

    private final TaskMapper taskMapper = TaskMapper.INSTANCE;

    @Test
    @DisplayName("Преобразование Task в TaskDTO")
    public void testToDTO() {
        Task task = new Task(1L, "Test Task", "Test Description", 1L, "NEW");

        TaskDTO taskDTO = taskMapper.toDTO(task);

        assertNotNull(taskDTO);
        assertEquals(task.getId(), taskDTO.getId());
        assertEquals(task.getTitle(), taskDTO.getTitle());
        assertEquals(task.getDescription(), taskDTO.getDescription());
        assertEquals(task.getUserId(), taskDTO.getUserId());
        assertEquals(task.getStatus(), taskDTO.getStatus());
    }

    @Test
    @DisplayName("Преобразование TaskDTO в Task")
    public void testToEntity() {
        TaskDTO taskDTO = new TaskDTO(1L, "Test Task", "Test Description", 1L, "NEW");

        Task task = taskMapper.toEntity(taskDTO);

        assertNotNull(task);
        assertEquals(taskDTO.getId(), task.getId());
        assertEquals(taskDTO.getTitle(), task.getTitle());
        assertEquals(taskDTO.getDescription(), task.getDescription());
        assertEquals(taskDTO.getUserId(), task.getUserId());
        assertEquals(taskDTO.getStatus(), task.getStatus());
    }

    @Test
    @DisplayName("Преобразование null Task в TaskDTO")
    public void testToDTONull() {
        TaskDTO taskDTO = taskMapper.toDTO(null);

        assertNull(taskDTO, "Преобразование null Task должно вернуть null TaskDTO");
    }

    @Test
    @DisplayName("Преобразование null TaskDTO в Task")
    public void testToEntityNull() {
        Task task = taskMapper.toEntity(null);

        assertNull(task, "Преобразование null TaskDTO должно вернуть null Task");
    }
}