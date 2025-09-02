package com.example.userServiceTask.exception.cardInfo;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String message) {
        super(message);
    }
}
