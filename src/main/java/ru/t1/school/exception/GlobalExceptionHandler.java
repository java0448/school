package ru.t1.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения TaskNotFoundException.
     *
     * @param ex исключение TaskNotFoundException
     * @return сообщение об ошибке
     */
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFoundException(TaskNotFoundException ex) {
        return ex.getMessage();
    }

    /**
     * Обрабатывает исключения TaskServiceException.
     *
     * @param ex исключение TaskServiceException
     * @return сообщение об ошибке
     */
    @ExceptionHandler(TaskServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleTaskServiceException(TaskServiceException ex) {
        return ex.getMessage();
    }

    /**
     * Обрабатывает все остальные исключения.
     *
     * @param ex общее исключение
     * @return сообщение об ошибке
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex) {
        return ex.getMessage();
    }
}