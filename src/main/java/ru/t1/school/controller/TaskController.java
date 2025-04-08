package ru.t1.school.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.school.dto.TaskDTO;
import ru.t1.school.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST-контроллер для управления задачами.
 */
@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Создает новую задачу.
     *
     * @param taskDTO задача для создания
     * @return созданная задача
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    /**
     * Получает задачу по ее ID.
     *
     * @param id ID задачи
     * @return задача, если найдена
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id ID задачи для обновления
     * @param taskDTO новые данные задачи
     * @return обновленная задача
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
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
    public List<TaskDTO> getAllTasks() {
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