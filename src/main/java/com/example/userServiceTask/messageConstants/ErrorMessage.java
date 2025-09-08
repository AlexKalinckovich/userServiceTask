package com.example.userServiceTask.messageConstants;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    RESOURCE_NOT_FOUND("resource_not_found"),
    EMAIL_ALREADY_EXISTS("email_already_exists"),
    CARD_ALREADY_EXISTS("card_already_exists"),
    CARD_OWNERSHIP_ERROR("card_ownership_error"),
    AUTHENTICATION_ERROR("authentication_error"),
    ACCESS_DENIED("access_denied"),
    JWT_ERROR("jwt_error"),
    VALIDATION_ERROR("validation_error"),
    ERROR_KEY("error_key"),
    DB_TRANSACTION_ERROR("db_transaction_error"),
    DB_INTEGRITY_VIOLATION("db_integrity_violation"),
    DB_QUERY_TIMEOUT("db_query_timeout"),
    DB_CONNECTION_FAILURE("db_connection_failure"),
    DB_DUPLICATE_KEY("db_duplicate_key"),
    DB_ERROR("db_error"),

    CACHE_ERROR("cache_error"),

    NETWORK_TIMEOUT("network_timeout"),
    NETWORK_UNREACHABLE("network_unreachable"),
    NETWORK_ERROR("network_error"),

    INTERNAL_ERROR("internal_error"),
    ILLEGAL_ARGUMENT("illegal_argument"),
    ILLEGAL_STATE("illegal_state");

    private final String key;

    ErrorMessage(final String key) {
        this.key = key;
    }
}