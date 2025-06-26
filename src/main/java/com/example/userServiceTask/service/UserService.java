package com.example.userServiceTask.service;

import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CachePut(value = "USER_CACHE", key = "#result.id")
    public User createUser(final User createUser) {

        if(userRepository.findByEmail(createUser.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with email already exists");
        }

        return userRepository.save(createUser);
    }

    @Transactional
    @CachePut(value = "USER_CACHE", key = "#result.id")
    public User updateUser(final UserUpdateDto userUpdateDto) {
        final Long id = userUpdateDto.getId();
        final Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userRepository.updateUserById(id, user.get());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "USER_CACHE", key = "#id")
    public User findUserById(final Long id) {
        final Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return user.get();
    }


    @Transactional
    @CacheEvict(value = "USER_CACHE", key = "#id")
    public User deleteUser(final Long id) {
        final Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userRepository.deleteUserById(id);
    }


}
