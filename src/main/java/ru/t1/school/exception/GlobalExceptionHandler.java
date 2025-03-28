package ru.t1.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Глобальный обработчик исключений для обработки исключений по всему приложению.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает TaskNotFoundException.
     *
     * @param ex исключение
     * @param request веб-запрос
     * @return ответ с сообщением об ошибке и HTTP-статусом
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleTaskNotFoundException(TaskNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает TaskServiceException.
     *
     * @param ex исключение
     * @param request веб-запрос
     * @return ответ с сообщением об ошибке и HTTP-статусом
     */
    @ExceptionHandler(TaskServiceException.class)
    public ResponseEntity<?> handleTaskServiceException(TaskServiceException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает все остальные исключения.
     *
     * @param ex исключение
     * @param request веб-запрос
     * @return ответ с сообщением об ошибке и HTTP-статусом
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}