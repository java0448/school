package ru.t1.school.exception;

/**
 * Исключение, выбрасываемое при ошибке сервиса.
 */
public class TaskServiceException extends RuntimeException {
    public TaskServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
