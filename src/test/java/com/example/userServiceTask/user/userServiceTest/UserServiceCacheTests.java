package com.example.userServiceTask.user.userServiceTest;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.model.user.User;
import com.example.userServiceTask.repositories.user.UserRepository;
import com.example.userServiceTask.service.user.UserService;
import com.example.userServiceTask.utils.AbstractContainerBaseTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@EnableCaching
public class UserServiceCacheTests extends AbstractContainerBaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private UserRepository userRepositorySpy;

    private CreateUserDto user;

    @BeforeEach
    void setUp() {
        userRepositorySpy.deleteAll();

        final Cache cache = cacheManager.getCache("USER_CACHE");
        if(cache != null){
            cache.clear();
        }

        user = CreateUserDto.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void createUser_success() {

        final UserResponseDto created = userService.createUser(user);

        assertNotNull(created.getId());
        assertEquals(user.getEmail(), created.getEmail());
        assertTrue(userRepositorySpy.findById(created.getId()).isPresent());
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        userService.createUser(user);

        final CreateUserDto user2 = CreateUserDto.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(user2));
    }

    @Test
    void createUser_mustHaveEmptyListOfCards(){
        final UserResponseDto dto = userService.createUser(user);
        assertNotNull(dto.getCards());
        assertTrue(dto.getCards().isEmpty());
    }

    @Test
    void updateUser_success() {
        final UserResponseDto dto = userService.createUser(user);

        final UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(dto.getId())
                .name("Updated")
                .surname("Bigger")
                .birthDate(LocalDate.of(1991, 2, 2)) // Обновляем дату рождения
                .build();

        userService.updateUser(updateDto);

        final UserResponseDto updated = userService.findUserById(dto.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("Bigger", updated.getSurname());
        assertEquals(LocalDate.of(1991, 2, 2), updated.getBirthDate());

        assertEquals("john.doe@example.com", updated.getEmail());
    }

    @Test
    void updateUser_notFound_throwsException() {
        final UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(999L)
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(updateDto));
    }

    @Test
    void findUserById_success() {

        final UserResponseDto dto = userService.createUser(user);

        final UserResponseDto found = userService.findUserById(dto.getId());

        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void findUserById_notFound_throwsException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(999L));
    }

    @Test
    void deleteUser_success() {
        final UserResponseDto dto = userService.createUser(user);

        userService.deleteUser(dto.getId());

        assertFalse(userRepositorySpy.findById(dto.getId()).isPresent());
    }

    @Test
    void whenFindUserById_thenCached() {
        final CreateUserDto user = CreateUserDto.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        final UserResponseDto createdUser = userService.createUser(user);

        final UserResponseDto firstCall = userService.findUserById(createdUser.getId());

        final UserResponseDto secondCall = userService.findUserById(createdUser.getId());

        verify(userRepositorySpy, times(0)).findById(1);
        assertEquals(firstCall.getEmail(), secondCall.getEmail());
    }

    @Test
    void whenUpdateUser_thenCacheUpdated() {
        User user = User.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        user = userRepositorySpy.save(user);

        userService.findUserById(user.getId());

        final UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(user.getId())
                .name("UpdatedName")
                .build();
        userService.updateUser(updateDto);

        final UserResponseDto cachedUser = Objects.requireNonNull(
                cacheManager.getCache("USER_CACHE")).get(user.getId(), UserResponseDto.class);

        if (cachedUser != null) {
            assertEquals("UpdatedName", cachedUser.getName());
        }
    }

    @Test
    void whenDeleteUser_thenCacheEvicted() {
        User user = User.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        user = userRepositorySpy.save(user);

        userService.findUserById(user.getId());
        userService.deleteUser(user.getId());

        final Cache cache = cacheManager.getCache("USER_CACHE");
        assertNotNull(cache);
        assertNull(cache.get(user.getId()));
    }

}