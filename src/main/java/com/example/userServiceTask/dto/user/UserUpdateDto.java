package com.example.userServiceTask.dto.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Data
@Builder
public class UserUpdateDto {
    @NotNull(message = "ID cannot be null")
    @Range(min = 1, message = "ID must be positive")
    private Long id;

    @Size(min = 2, max = 25, message = "Name must be between 2 and 25 characters")
    private String name;

    @Size(min = 2, max = 25, message = "Surname must be between 2 and 25 characters")
    private String surname;

    @Email(message = "Email should be valid")
    @Size(max = 40, message = "Email must be up to 40 characters")
    private String email;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}
