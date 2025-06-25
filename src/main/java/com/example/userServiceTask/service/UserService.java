package com.example.userServiceTask.service;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.mappers.user.UserMapper;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDto createUser(final CreateUserDto createUserDto) {

        if(userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with email already exists");
        }

        final User user = userMapper.createFromDto(createUserDto);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDto updateUser(final UserUpdateDto userUpdateDto) {
        final Long id = userUpdateDto.getId();
        final Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        final User updatedUser = user.get();
        userMapper.updateFromDto(userUpdateDto, updatedUser);

        return userMapper.toResponseDto(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(final Long id) {
        final Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userMapper.toResponseDto(user.get());
    }


    @Transactional
    public UserResponseDto deleteUser(final Long id) {
        final Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userMapper.toResponseDto(userRepository.deleteUserById(id));
    }


}
