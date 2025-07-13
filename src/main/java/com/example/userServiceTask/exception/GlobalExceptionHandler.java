package com.example.userServiceTask.exception;


import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.messageConstants.ErrorMessage;
import com.example.userServiceTask.service.messages.MessageService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
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

    private final MessageService messageService;
    private final ExceptionResponseService exceptionResponseService;

    public GlobalExceptionHandler(final MessageService messageService,
                                  final ExceptionResponseService exceptionResponseService) {
        this.messageService = messageService;
        this.exceptionResponseService = exceptionResponseService;
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final @NotNull HttpHeaders headers,
                                                                  final @NotNull HttpStatusCode status,
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
                messageService.getMessage(ErrorMessage.VALIDATION_ERROR),
                request.getDescription(false),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            final EmailAlreadyExistsException ex,
            final WebRequest request) {

        return exceptionResponseService.buildWebRequestErrorResponse(
                ex, request,
                HttpStatus.CONFLICT,
                messageService.getMessage(ErrorMessage.EMAIL_ALREADY_EXISTS)
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            final EntityNotFoundException ex,
            final WebRequest request) {

        return exceptionResponseService.buildWebRequestErrorResponse(
                ex, request,
                HttpStatus.NOT_FOUND,
                messageService.getMessage(ErrorMessage.RESOURCE_NOT_FOUND)
        );
    }



}
