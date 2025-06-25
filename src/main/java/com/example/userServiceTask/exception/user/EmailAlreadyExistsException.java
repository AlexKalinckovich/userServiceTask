package com.example.userServiceTask.exception.user;


public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(final String message) {
        super(message);
    }

}
