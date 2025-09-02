package com.example.userServiceTask.exception;


import com.example.userServiceTask.exception.cardInfo.CardAlreadyExistsException;
import com.example.userServiceTask.exception.cardInfo.CardOwnershipException;
import com.example.userServiceTask.exception.response.ErrorResponse;
import com.example.userServiceTask.exception.response.ExceptionResponseService;
import com.example.userServiceTask.exception.response.ValidationErrorDetails;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.messageConstants.ErrorMessage;
import com.example.userServiceTask.service.messages.MessageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.QueryTimeoutException;
import org.hibernate.cache.CacheException;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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

    // -------------------------------
    // Validation errors
    // -------------------------------
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final @NotNull HttpHeaders headers,
            final @NotNull HttpStatusCode status,
            final @NotNull WebRequest request) {

        final Map<String, String> fieldErrors = new HashMap<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        final ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorMessage.VALIDATION_ERROR.name(),
                messageService.getMessage(ErrorMessage.VALIDATION_ERROR),
                request.getDescription(false),
                new ValidationErrorDetails(fieldErrors)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------
    // Business / domain errors
    // -------------------------------
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            final EmailAlreadyExistsException ex,
            final WebRequest request) {

        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                ErrorMessage.EMAIL_ALREADY_EXISTS
        );
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCardAlreadyExists(
            final CardAlreadyExistsException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                ErrorMessage.CARD_ALREADY_EXISTS
        );
    }

    @ExceptionHandler(CardOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleCardOwnership(
            final CardOwnershipException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.FORBIDDEN,
                ErrorMessage.CARD_OWNERSHIP_ERROR
        );
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            final EntityNotFoundException ex,
            final WebRequest request) {

        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.NOT_FOUND,
                ErrorMessage.RESOURCE_NOT_FOUND
        );
    }

    // -------------------------------
    // Database / persistence errors
    // -------------------------------
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystemException(
            final TransactionSystemException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorMessage.DB_TRANSACTION_ERROR
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrityViolation(
            final DataIntegrityViolationException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                ErrorMessage.DB_INTEGRITY_VIOLATION
        );
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleQueryTimeout(
            final QueryTimeoutException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.REQUEST_TIMEOUT,
                ErrorMessage.DB_QUERY_TIMEOUT
        );
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResponse> handleResourceFailure(
            final DataAccessResourceFailureException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorMessage.DB_CONNECTION_FAILURE
        );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKey(
            final DuplicateKeyException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                ErrorMessage.DB_DUPLICATE_KEY
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleGenericDataAccess(
            final DataAccessException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessage.DB_ERROR
        );
    }

    // -------------------------------
    // Cache errors
    // -------------------------------
    @ExceptionHandler(CacheException.class)
    public ResponseEntity<ErrorResponse> handleCacheError(
            final CacheException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessage.CACHE_ERROR
        );
    }

    // -------------------------------
    // Network errors
    // -------------------------------
    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleSocketTimeout(
            final SocketTimeoutException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.GATEWAY_TIMEOUT,
                ErrorMessage.NETWORK_TIMEOUT
        );
    }

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<ErrorResponse> handleUnknownHost(
            final UnknownHostException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorMessage.NETWORK_UNREACHABLE
        );
    }

    // -------------------------------
    // General Java errors
    // -------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            final IllegalArgumentException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.BAD_REQUEST,
                ErrorMessage.ILLEGAL_ARGUMENT
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            final IllegalStateException ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                ErrorMessage.ILLEGAL_STATE
        );
    }

    // -------------------------------
    // Fallback for unexpected errors
    // -------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            final Exception ex,
            final WebRequest request) {
        return exceptionResponseService.buildErrorResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessage.INTERNAL_ERROR
        );
    }
}
