package ru.t1.school.exception;

public class TaskServiceException extends RuntimeException {
    public TaskServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
