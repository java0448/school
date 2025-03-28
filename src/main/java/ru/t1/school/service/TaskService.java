package ru.t1.school.service;

import ru.t1.school.entity.Task;
import ru.t1.school.exception.TaskNotFoundException;
import ru.t1.school.exception.TaskServiceException;
import ru.t1.school.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный класс для управления задачами.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Создает новую задачу.
     *
     * @param task задача для создания
     * @return созданная задача
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
     * @return задача, если найдена
     */
    public Optional<Task> getTaskById(Long id) {
        try {
            return taskRepository.findById(id);
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
     */
    public Task updateTask(Long id, Task taskDetails) {
        try {
            Optional<Task> task = taskRepository.findById(id);
            if (task.isPresent()) {
                Task existingTask = task.get();
                existingTask.setTitle(taskDetails.getTitle());
                existingTask.setDescription(taskDetails.getDescription());
                existingTask.setUserId(taskDetails.getUserId());
                return taskRepository.save(existingTask);
            } else {
                throw new TaskNotFoundException("Task not found with id " + id);
            }
        } catch (Exception e) {
            throw new TaskServiceException("Failed to update task", e);
        }
    }

    /**
     * Удаляет задачу по ее ID.
     *
     * @param id ID задачи для удаления
     * @return true, если задача была удалена, false в противном случае
     */
    public boolean deleteTask(Long id) {
        try {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
                return true;
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
     */
    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAll();
        } catch (Exception e) {
            throw new TaskServiceException("Failed to retrieve tasks", e);
        }
    }
}