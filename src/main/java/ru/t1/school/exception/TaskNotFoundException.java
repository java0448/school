package ru.t1.school.exception;

/**
 * Исключение, выбрасываемое, когда задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {
    /**
     * Создает новое исключение TaskNotFoundException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public TaskNotFoundException(String message) {
        super(message);
    }
}
