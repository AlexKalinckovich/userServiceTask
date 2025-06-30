package com.example.userServiceTask.UserServiceTest;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.exception.user.EmailAlreadyExistsException;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.repositories.UserRepository;
import com.example.userServiceTask.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UserServiceCacheTests {

    private static final String REDIS_IMAGE = "redis:7.0-alpine";
    private static final String MYSQL_IMAGE = "mysql:8.0";
    private static final int REDIS_PORT = 6379;
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_DATABASE_NAME = "userdb";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "wtpassword";

    private static final String RESOURCE_PATH = "db/changelog";
    private static final String INITIAL_SCHEMA = RESOURCE_PATH + "/v1-initial-schema.xml";

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
            .withDatabaseName(MYSQL_DATABASE_NAME)
            .withUsername(MYSQL_USERNAME)
            .withPassword(MYSQL_PASSWORD)
            .withExposedPorts(MYSQL_PORT)
            .withReuse(true)
            .withCopyToContainer(
                    MountableFile.forClasspathResource(RESOURCE_PATH),
                    INITIAL_SCHEMA
            );

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private UserRepository userRepositorySpy;

    private CreateUserDto user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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

        UserResponseDto created = userService.createUser(user);

        assertNotNull(created.getId());
        assertEquals(user.getEmail(), created.getEmail());
        assertTrue(userRepositorySpy.findById(created.getId()).isPresent());
    }

    // Тест создания пользователя с существующим email
    @Test
    void createUser_duplicateEmail_throwsException() {
        userService.createUser(user);

        CreateUserDto user2 = CreateUserDto.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(user2));
    }

    @Test
    void updateUser_success() {
        UserResponseDto dto = userService.createUser(user);

        UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(dto.getId())
                .name("Updated")
                .surname("Bigger")
                .birthDate(LocalDate.of(1991, 2, 2)) // Обновляем дату рождения
                .build(); // Не обновляем email

        userService.updateUser(updateDto);

        final UserResponseDto updated = userService.findUserById(dto.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("Bigger", updated.getSurname());
        assertEquals(LocalDate.of(1991, 2, 2), updated.getBirthDate());

        assertEquals("john.doe@example.com", updated.getEmail());
    }

    // Тест обновления несуществующего пользователя
    @Test
    void updateUser_notFound_throwsException() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(999L)
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(updateDto));
    }

    // Тест поиска пользователя
    @Test
    void findUserById_success() {

        final UserResponseDto dto = userService.createUser(user);

        UserResponseDto found = userService.findUserById(dto.getId());

        assertEquals(user.getEmail(), found.getEmail());
    }

    // Тест поиска несуществующего пользователя
    @Test
    void findUserById_notFound_throwsException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(999L));
    }

    // Тест удаления пользователя
    @Test
    void deleteUser_success() {
        final UserResponseDto dto = userService.createUser(user);

        userService.deleteUser(dto.getId());

        assertFalse(userRepositorySpy.findById(dto.getId()).isPresent());
    }

    // Тест кэширования при чтении
    @Test
    void whenFindUserById_thenCached() {
        CreateUserDto user = CreateUserDto.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        UserResponseDto createdUser = userService.createUser(user);

        UserResponseDto firstCall = userService.findUserById(createdUser.getId());

        UserResponseDto secondCall = userService.findUserById(createdUser.getId());

        verify(userRepository, times(0)).findById(1);
        assertEquals(firstCall.getEmail(), secondCall.getEmail());
    }

    // Тест обновления кэша при изменении
    @Test
    void whenUpdateUser_thenCacheUpdated() {
        User user = User.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        user = userRepositorySpy.save(user);

        // Первый вызов - кэшируем
        userService.findUserById(user.getId());

        // Обновляем пользователя
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(user.getId())
                .name("UpdatedName")
                .build();
        userService.updateUser(updateDto);

        // Проверяем кэш
        UserResponseDto cachedUser = Objects.requireNonNull(
                cacheManager.getCache("USER_CACHE")).get(user.getId(), UserResponseDto.class);

        assertEquals("UpdatedName", cachedUser.getName());
    }

    // Тест очистки кэша при удалении
    @Test
    void whenDeleteUser_thenCacheEvicted() {
        User user = User.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        user = userRepositorySpy.save(user);

        // Заполняем кэш
        userService.findUserById(user.getId());
        // Удаляем пользователя
        userService.deleteUser(user.getId());

        // Проверяем, что в кэше нет записи
        Cache cache = cacheManager.getCache("USER_CACHE");
        assertNotNull(cache);
        assertNull(cache.get(user.getId()));
    }

}