package com.example.userServiceTask.service.user;

import com.example.userServiceTask.dto.user.UserCreateDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.mappers.user.UserMapper;
import com.example.userServiceTask.model.user.User;
import com.example.userServiceTask.repositories.user.UserRepository;
import com.example.userServiceTask.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService  {

    private static final String USER_CACHE = "USER_CACHE";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;


    @Transactional
    @CachePut(value = USER_CACHE, key = "#result.id")
    public UserResponseDto createUser(final UserCreateDto createUser) {
        final User createdUser = userValidator.validateCreateDto(createUser);
        return userMapper.toResponseDto(userRepository.save(createdUser));
    }

    @Transactional
    @CachePut(value = USER_CACHE, key = "#userUpdateDto.id")
    public UserResponseDto updateUser(final UserUpdateDto userUpdateDto) {
        final User user = userValidator.validateUpdateDto(userUpdateDto);
        userMapper.updateFromDto(userUpdateDto, user);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = USER_CACHE, key = "#id")
    public UserResponseDto findUserById(final Long id) {
        final User user = userValidator.checkUserForExistenceById(id);
        return userMapper.toResponseDto(user);
    }


    @Transactional
    @CacheEvict(value = USER_CACHE, key = "#id")
    public UserResponseDto deleteUser(final Long id) {
        final User user =userValidator.checkUserForExistenceById(id);
        userRepository.deleteUserById(id);
        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    @CachePut(value = USER_CACHE, key = "#email")
    public UserResponseDto findUserByEmail(final String email) {
        final User user = userValidator.checkUserForExistenceByEmail(email);
        return userMapper.toResponseDto(user);
    }
}
