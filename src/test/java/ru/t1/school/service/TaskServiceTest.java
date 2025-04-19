package ru.t1.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.school.dto.TaskDTO;
import ru.t1.school.dto.TaskStatusDTO;
import ru.t1.school.entity.Task;
import ru.t1.school.exception.TaskNotFoundException;
import ru.t1.school.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTemplate<String, TaskStatusDTO> kafkaTemplate;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;

    @Value("${kafka.topic.client}")
    private String taskStatusTopic;

    @BeforeEach
    public void setUp() {
        task = new Task(1L, "Test Task", "Test Description", 1L, "NEW");
        taskDTO = new TaskDTO(1L, "Test Task", "Test Description", 1L, "NEW");

        // Устанавливаем значение переменной taskStatusTopic в taskService
        taskService = new TaskService(taskRepository, kafkaTemplate, taskStatusTopic);
    }

    @Test
    @DisplayName("Создание задачи")
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO createdTask = taskService.createTask(taskDTO);

        assertNotNull(createdTask);
        assertEquals(taskDTO.getTitle(), createdTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Получение задачи по ID")
    public void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO foundTask = taskService.getTaskById(1L);

        assertNotNull(foundTask);
        assertEquals(taskDTO.getTitle(), foundTask.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Получение задачи по ID - задача не найдена")
    public void testGetTaskByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        assertEquals("Task not found with id 1", thrown.getMessage());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Обновление задачи")
    public void testUpdateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updatedTask = taskService.updateTask(1L, taskDTO);

        assertNotNull(updatedTask);
        assertEquals(taskDTO.getTitle(), updatedTask.getTitle());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(kafkaTemplate, times(1)).send(eq(taskStatusTopic), any(TaskStatusDTO.class)); // Проверка правильности темы
    }

    @Test
    @DisplayName("Обновление задачи - задача не найдена")
    public void testUpdateTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, taskDTO));
        assertEquals(String.format(TaskNotFoundException.MESSAGE_TEMPLATE, 1L), thrown.getMessage());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Удаление задачи")
    public void testDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление задачи - задача не найдена")
    public void testDeleteTaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        assertEquals(String.format(TaskNotFoundException.MESSAGE_TEMPLATE, 1L), thrown.getMessage());
        verify(taskRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Получение всех задач")
    public void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }
}