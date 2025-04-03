package ru.t1.school.controller;

import ru.t1.school.entity.Task;
import ru.t1.school.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления задачами.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Создает новую задачу.
     *
     * @param task задача для создания
     * @return созданная задача
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    /**
     * Получает задачу по ее ID.
     *
     * @param id ID задачи
     * @return задача, если найдена
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id ID задачи для обновления
     * @param taskDetails новые данные задачи
     * @return обновленная задача
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskService.updateTask(id, taskDetails);
    }

    /**
     * Удаляет задачу по ее ID.
     *
     * @param id ID задачи для удаления
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    /**
     * Получает все задачи.
     *
     * @return список задач
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Выбрасывает тестовое исключение для демонстрации обработки исключений.
     */
    @GetMapping("/exception")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void throwException() {
        throw new RuntimeException("This is a test exception");
    }
}