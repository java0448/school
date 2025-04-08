package ru.t1.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import ru.t1.school.dto.TaskDTO;
import ru.t1.school.dto.TaskStatusDTO;
import ru.t1.school.entity.Task;
import ru.t1.school.exception.TaskNotFoundException;
import ru.t1.school.exception.TaskServiceException;
import ru.t1.school.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления задачами.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, TaskStatusDTO> kafkaTemplate;
    private final String taskStatusTopic;

    @Autowired
    public TaskService(TaskRepository taskRepository, KafkaTemplate<String, TaskStatusDTO> kafkaTemplate,
                       @Value("${kafka.topic.client}") String taskStatusTopic) {
        this.taskRepository = taskRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.taskStatusTopic = taskStatusTopic;
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        try {
            logger.info("Creating task with title: {}", taskDTO.getTitle());
            Task task = convertToEntity(taskDTO);
            if (task.getStatus() == null) {
                task.setStatus("NEW"); // Установка значения по умолчанию, если оно не передано
            }
            Task createdTask = taskRepository.save(task);
            logger.info("Task created with ID: {}", createdTask.getId());
            return convertToDTO(createdTask);
        } catch (Exception e) {
            logger.error("Failed to create task", e);
            throw new TaskServiceException("Failed to create task", e);
        }
    }

    public TaskDTO getTaskById(Long id) {
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
            return convertToDTO(task);
        } catch (Exception e) {
            logger.error("Failed to retrieve task with ID: {}", id, e);
            throw new TaskServiceException("Failed to retrieve task", e);
        }
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        try {
            Task existingTask = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
            existingTask.setTitle(taskDTO.getTitle());
            existingTask.setDescription(taskDTO.getDescription());
            existingTask.setUserId(taskDTO.getUserId());
            existingTask.setStatus("UPDATE"); // Изменение статуса на UPDATE
            Task updatedTask = taskRepository.save(existingTask);

            // Отправка сообщения в Kafka
            TaskStatusDTO taskStatusDTO = new TaskStatusDTO(id, "UPDATE", "Task updated with status 'UPDATE'");
            kafkaTemplate.send(taskStatusTopic, taskStatusDTO);

            return convertToDTO(updatedTask);
        } catch (Exception e) {
            logger.error("Failed to update task with ID: {}", id, e);
            throw new TaskServiceException("Failed to update task", e);
        }
    }

    public void deleteTask(Long id) {
        try {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
            } else {
                throw new TaskNotFoundException("Task not found with id " + id);
            }
        } catch (Exception e) {
            logger.error("Failed to delete task with ID: {}", id, e);
            throw new TaskServiceException("Failed to delete task", e);
        }
    }

    public List<TaskDTO> getAllTasks() {
        try {
            return taskRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve tasks", e);
            throw new TaskServiceException("Failed to retrieve tasks", e);
        }
    }

    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getUserId(), task.getStatus());
    }

    private Task convertToEntity(TaskDTO taskDTO) {
        return new Task(taskDTO.getId(), taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getUserId(), taskDTO.getStatus());
    }
}