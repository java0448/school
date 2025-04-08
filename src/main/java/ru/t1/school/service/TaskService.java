package ru.t1.school.service;

import org.springframework.beans.factory.annotation.Value;
import ru.t1.school.dto.TaskStatusDTO;
import ru.t1.school.entity.Task;
import ru.t1.school.exception.TaskNotFoundException;
import ru.t1.school.exception.TaskServiceException;
import ru.t1.school.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления задачами.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
//    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, TaskStatusDTO> kafkaTemplate;
    private final String taskStatusTopic;

    @Autowired
//    public TaskService(TaskRepository taskRepository, KafkaTemplate<String, String> kafkaTemplate, @Value("${kafka.topic.client}") String taskStatusTopic) {
    public TaskService(TaskRepository taskRepository, KafkaTemplate<String, TaskStatusDTO> kafkaTemplate,
                       @Value("${kafka.topic.client}") String taskStatusTopic) {
        this.taskRepository = taskRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.taskStatusTopic = taskStatusTopic;
    }

    /**
     * Создает новую задачу.
     *
     * @param task задача для создания
     * @return созданная задача
     * @throws TaskServiceException если не удалось создать задачу
     */
    public Task createTask(Task task) {
        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            throw new TaskServiceException("Failed to create task", e);
        }
    }

    /**
     * Получает задачу по ее ID.
     *
     * @param id ID задачи
     * @return найденная задача
     * @throws TaskNotFoundException если задача не найдена
     * @throws TaskServiceException если не удалось получить задачу
     */
    public Task getTaskById(Long id) {
        try {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
        } catch (Exception e) {
            throw new TaskServiceException("Failed to retrieve task", e);
        }
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id ID задачи для обновления
     * @param taskDetails новые данные задачи
     * @return обновленная задача
     * @throws TaskNotFoundException если задача не найдена
     * @throws TaskServiceException если не удалось обновить задачу
     */
    public Task updateTask(Long id, Task taskDetails) {
        try {
            Task existingTask = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setUserId(taskDetails.getUserId());
            existingTask.setStatus("UPDATE"); // Изменение статуса на UPDATE
            Task updatedTask = taskRepository.save(existingTask);

            // Отправка сообщения в Kafka
//            kafkaTemplate.send(taskStatusTopic, id.toString(), "Task updated with status 'UPDATE'");
            TaskStatusDTO taskStatusDTO = new TaskStatusDTO(id, "UPDATE", "Task updated with status 'UPDATE'");
            kafkaTemplate.send(taskStatusTopic, taskStatusDTO);

            return updatedTask;
        } catch (Exception e) {
            throw new TaskServiceException("Failed to update task", e);
        }
    }

    /**
     * Удаляет задачу по ее ID.
     *
     * @param id ID задачи для удаления
     * @throws TaskNotFoundException если задача не найдена
     * @throws TaskServiceException если не удалось удалить задачу
     */
    public void deleteTask(Long id) {
        try {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
            } else {
                throw new TaskNotFoundException("Task not found with id " + id);
            }
        } catch (Exception e) {
            throw new TaskServiceException("Failed to delete task", e);
        }
    }

    /**
     * Получает все задачи.
     *
     * @return список задач
     * @throws TaskServiceException если не удалось получить задачи
     */
    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAll();
        } catch (Exception e) {
            throw new TaskServiceException("Failed to retrieve tasks", e);
        }
    }
}