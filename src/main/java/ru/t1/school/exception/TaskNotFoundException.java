package ru.t1.school.exception;

/**
 * Исключение, выбрасываемое, когда задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {

    public static final String MESSAGE_TEMPLATE = "Task not found with id %d";
    /**
     * Создает новое исключение TaskNotFoundException с указанным идентификатором задачи.
     *
     * @param id идентификатор задачи, которая не найдена
     */
    public TaskNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE, id));
    }
}
