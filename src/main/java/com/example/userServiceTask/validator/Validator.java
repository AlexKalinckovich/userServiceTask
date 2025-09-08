package com.example.userServiceTask.validator;

public interface Validator<C, U, R> {
    R validateCreateDto(C createDto);

    R validateUpdateDto(U updateDto);
}
