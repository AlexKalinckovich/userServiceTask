package com.example.userServiceTask.service.messages;

import com.example.userServiceTask.messageConstants.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {
    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(ErrorMessage errorMessage) {
        return messageSource.getMessage(errorMessage.getKey(), null, Locale.ENGLISH);
    }
}
