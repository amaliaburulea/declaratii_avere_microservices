package com.declaratiiavere.iam.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;

/**
 * @author Viorel Vesa.
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFound(RuntimeException e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorWrapper(e.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<Object> handleValidationErrors(RuntimeException e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorWrapper(e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleDefaultErrors(RuntimeException e, WebRequest request) {
        return handleExceptionInternal(e, new ErrorWrapper(e.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }
}
