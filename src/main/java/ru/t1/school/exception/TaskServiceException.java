package ru.t1.school.exception;

/**
 * Исключение, выбрасываемое при ошибке сервиса.
 */
public class TaskServiceException extends RuntimeException {
    /**
     * Создает новое исключение TaskServiceException с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public TaskServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
