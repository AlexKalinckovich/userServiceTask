package com.example.userServiceTask.validator.user;

import com.example.userServiceTask.dto.user.UserCreateDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.exception.user.UserNotFoundException;
import com.example.userServiceTask.mappers.user.UserMapper;
import com.example.userServiceTask.model.user.User;
import com.example.userServiceTask.repositories.user.UserRepository;
import com.example.userServiceTask.validator.Validator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator<UserCreateDto, UserUpdateDto, User> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User validateCreateDto(final @NotNull UserCreateDto createDto) {
        final String email = createDto.getEmail();
        if(userRepository.existsByEmail(email)){
            throw new ValidationException("Email already exists");
        }
        return userMapper.createFromDto(createDto);
    }

    @Override
    public User validateUpdateDto(final @NotNull UserUpdateDto updateDto) {

        final Long id = updateDto.getId();
        final User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found")
                );

        final String email = updateDto.getEmail();
        if(email != null){

            if(userRepository.existsByEmail(email)){
               throw new EmailAlreadyExistsException("Email already exists");
            }

        }

        return user;
    }

    public User checkUserForExistenceById(final Long id){
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found")
                );
    }

    public User checkUserForExistenceByEmail(final String email){
        return userRepository.findUserByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found")
                );
    }

}
