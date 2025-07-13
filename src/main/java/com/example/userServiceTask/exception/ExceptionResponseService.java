package com.example.userServiceTask.exception;

import com.example.userServiceTask.messageConstants.ErrorMessage;
import com.example.userServiceTask.service.messages.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExceptionResponseService {

    private final MessageService messageService;

    public ResponseEntity<ErrorResponse> buildWebRequestErrorResponse(final Exception ex,
                                                                      final WebRequest request,
                                                                      final HttpStatus status,
                                                                      final String message){
        final ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                message,
                request.getDescription(false),
                Map.of(messageService.getMessage(ErrorMessage.ERROR_KEY), ex.getMessage())
        );

        return new ResponseEntity<>(errorResponse, status);
    }

}