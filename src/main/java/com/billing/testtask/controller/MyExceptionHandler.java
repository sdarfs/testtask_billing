package com.billing.testtask.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class MyExceptionHandler {

    /**
     * Общий обработчик всех ошибок, возникающих при обработке запросов
     * @param exception обработанное исключение
     * @return вывод страницы со статусом обработки и сообщением
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        if (isSwaggerRequest(exception)) {
            return null;
        }

        String status = "Error caused by exception: " + exception.getMessage();
        return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Проверяет, относится ли исключение к Swagger-запросу
     */
    private boolean isSwaggerRequest(Exception exception) {
        if (exception instanceof NoHandlerFoundException) {
            String requestPath = ((NoHandlerFoundException) exception).getRequestURL();
            return requestPath.contains("/swagger-ui") ||
                    requestPath.contains("/api-docs") ||
                    requestPath.contains("/v3/api-docs");
        }
        return false;
    }

    /**
     * Специальный обработчик для 404 ошибок
     */

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>("Resource not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}