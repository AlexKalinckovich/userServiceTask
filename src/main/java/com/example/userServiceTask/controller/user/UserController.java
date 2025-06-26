package com.example.userServiceTask.controller.user;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.mappers.user.UserMapper;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @Autowired
    public UserController(final UserService userService,
                          final UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        final UserResponseDto createdUser = userMapper.toResponseDto(
                userService.createUser(
                        userMapper.createFromDto(createUserDto)
                )
        );

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        final User updatedUser = userService.updateUser(userUpdateDto);
        return ResponseEntity.ok(userMapper.toResponseDto(updatedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable long id) {
        final User user = userService.findUserById(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long id) {
        final User user = userService.deleteUser(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }
}
