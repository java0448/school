package ru.t1.school.service;

import ru.t1.school.entity.Task;
import ru.t1.school.exception.TaskNotFoundException;
import ru.t1.school.exception.TaskServiceException;
import ru.t1.school.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            throw new TaskServiceException("Failed to create task", e);
        }
    }

    public Optional<Task> getTaskById(Long id) {
        try {
            return taskRepository.findById(id);
        } catch (Exception e) {
            throw new TaskServiceException("Failed to retrieve task", e);
        }
    }

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

    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAll();
        } catch (Exception e) {
            throw new TaskServiceException("Failed to retrieve tasks", e);
        }
    }
}