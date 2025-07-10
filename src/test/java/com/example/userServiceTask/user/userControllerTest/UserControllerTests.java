package com.example.userServiceTask.user.userControllerTest;

import com.example.userServiceTask.controller.user.UserController;
import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;
import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.messageConstants.ErrorMessage;
import com.example.userServiceTask.service.messages.MessageService;
import com.example.userServiceTask.service.user.UserService;
import com.example.userServiceTask.utils.AbstractContainerBaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests extends AbstractContainerBaseTest {

    private final MessageService messageService;

    @Autowired
    public UserControllerTests(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final CreateUserDto validCreateDto = CreateUserDto.builder()
            .name("John")
            .surname("Doe")
            .email("john.doe@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    private final UserResponseDto sampleResponse = UserResponseDto.builder()
            .id(1L)
            .name("John")
            .surname("Doe")
            .email("john.doe@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .cards(Collections.emptyList())
            .build();



    @Test
    void createUser_ValidInput_ReturnsCreated() throws Exception {
        Mockito.when(userService.createUser(any(CreateUserDto.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/users/create/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.error").value("john.doe@example.com"));
    }

    @Test
    void createUser_InvalidInput_ReturnsBadRequest() throws Exception {
        final CreateUserDto invalidDto = CreateUserDto.builder()
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(messageService.getMessage(ErrorMessage.VALIDATION_ERROR)))
                .andExpect(jsonPath("$.details.email").value("must not be blank"));
    }

    @Test
    void getUser_ExistingId_ReturnsUser() throws Exception {
        Mockito.when(userService.findUserById(1L))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void getUser_NonExistingId_ReturnsNotFound() throws Exception {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(messageService.getMessage(ErrorMessage.RESOURCE_NOT_FOUND)));
    }

    @Test
    void updateUser_ValidInput_ReturnsUpdatedUser() throws Exception {
        final UserUpdateDto updateDto = UserUpdateDto.builder()
                .id(1L)
                .name("Jane")
                .surname("Smith")
                .birthDate(LocalDate.of(1995, 5, 15))
                .build();
        final UserResponseDto updatedResponse = UserResponseDto.builder()
                .id(1L)
                .name("Jane")
                .surname("Smith")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1995, 5, 15))
                .build();

        Mockito.when(userService.updateUser(any(UserUpdateDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.birthDate").value("1995-05-15"));
    }

    @Test
    void deleteUser_ExistingId_ReturnsSuccess() throws Exception {
        Mockito.when(userService.deleteUser(1L))
                .thenReturn(1);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void deleteUser_NonExistingId_ReturnsZero() throws Exception {
        Mockito.when(userService.deleteUser(999L))
                .thenReturn(0);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
