package com.example.userServiceTask.exception.response;

sealed interface ErrorDetails permits ValidationErrorDetails, SimpleErrorDetails {
}
