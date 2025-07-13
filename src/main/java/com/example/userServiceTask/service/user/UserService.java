package com.example.userServiceTask.service.user;

import com.example.dto.user.CreateUserDto;
import com.example.dto.user.UserResponseDto;
import com.example.dto.user.UserUpdateDto;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.mappers.user.UserMapper;
import com.example.userServiceTask.model.user.User;
import com.example.userServiceTask.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(final UserRepository userRepository,
                       final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @CachePut(value = "USER_CACHE", key = "#result.id")
    public UserResponseDto createUser(final CreateUserDto createUser) {

        if(userRepository.existsByEmail(createUser.getEmail())) {
            throw new EmailAlreadyExistsException("User with email already exists");
        }

        final User createdUser = userMapper.createFromDto(createUser);
        return userMapper.toResponseDto(userRepository.save(createdUser));
    }

    @Transactional
    @CachePut(value = "USER_CACHE", key = "#userUpdateDto.id")
    public UserResponseDto updateUser(final UserUpdateDto userUpdateDto) {
        final Long id = userUpdateDto.getId();
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateFromDto(userUpdateDto, user);

        final String dtoEmail = userUpdateDto.getEmail();
        final String userEmail = user.getEmail();
        if(dtoEmail != null && !userEmail.equals(dtoEmail)) {
            if(userRepository.existsByEmail(dtoEmail)) {
                throw new EmailAlreadyExistsException("User with email already exists");
            }
        }

        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "USER_CACHE", key = "#id")
    public UserResponseDto findUserById(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return userMapper.toResponseDto(user);
    }


    @Transactional
    @CacheEvict(value = "USER_CACHE", key = "#id")
    public int deleteUser(final Long id) {

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userRepository.deleteUserById(id);
    }

}
