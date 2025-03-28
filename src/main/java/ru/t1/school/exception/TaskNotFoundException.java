package ru.t1.school.exception;

/**
 * Исключение, выбрасываемое, когда задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
