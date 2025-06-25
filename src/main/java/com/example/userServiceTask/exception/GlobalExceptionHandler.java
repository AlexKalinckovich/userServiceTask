package com.example.userServiceTask.exception;


import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {

        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        final ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getDescription(false),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            final EmailAlreadyExistsException ex,
            final WebRequest request) {

        return buildErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                "Email already registered"
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            final EntityNotFoundException ex,
            final WebRequest request) {

        return buildErrorResponse(
                ex,
                request,
                HttpStatus.NOT_FOUND,
                "Resource not found"
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            final Exception ex,
            final WebRequest request,
            final HttpStatus status,
            final String message) {

        final ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                message,
                request.getDescription(false),
                Map.of("error", ex.getMessage())
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
