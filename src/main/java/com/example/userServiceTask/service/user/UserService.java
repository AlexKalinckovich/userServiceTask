package com.example.userServiceTask.service.user;

import com.example.userServiceTask.dto.user.UserCreateDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;

public interface UserService {
    UserResponseDto createUser(final UserCreateDto createUser);
    UserResponseDto updateUser(final UserUpdateDto userUpdateDto);
    UserResponseDto deleteUser(final Long id);
    UserResponseDto findUserById(final Long id);
    UserResponseDto findUserByEmail(final String email);
}
