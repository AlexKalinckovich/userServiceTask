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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CachePut(value = "USER_CACHE", key = "#result.id")
    public UserResponseDto createUser(final CreateUserDto createUser) {

        if(userRepository.findByEmail(createUser.getEmail()).isPresent()) {
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

        updateUserByUpdateDto(user, userUpdateDto);
        userRepository.updateUser(id, user);
        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "USER_CACHE", key = "#id")
    public UserResponseDto findUserById(final Long id) {
        final Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userMapper.toResponseDto(user.get());
    }


    @Transactional
    @CacheEvict(value = "USER_CACHE", key = "#id")
    public int deleteUser(final Long id) {
        final Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userRepository.deleteUserById(id);
    }


    private void updateUserByUpdateDto(final User user, final UserUpdateDto updateDto) {
        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            user.setName(updateDto.getName());
        }

        if (updateDto.getSurname() != null && !updateDto.getSurname().isBlank()) {
            user.setSurname(updateDto.getSurname());
        }

        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            if (!user.getEmail().equals(updateDto.getEmail()) &&
                    userRepository.existsByEmail(updateDto.getEmail())) {
                throw new EmailAlreadyExistsException("Email already in use");
            }
            user.setEmail(updateDto.getEmail());
        }

        if (updateDto.getBirthDate() != null) {
            user.setBirthDate(updateDto.getBirthDate());
        }
    }

}
