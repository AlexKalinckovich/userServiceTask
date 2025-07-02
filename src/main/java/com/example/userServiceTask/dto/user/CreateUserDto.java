package com.example.userServiceTask.dto.user;


import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateUserDto {

    @Size(min = 2, max = 25)
    @NotNull
    @NotBlank
    private String name;

    @Size(min = 2, max = 25)
    @NotBlank
    private String surname;

    @Size(min = 2, max = 25)
    @Email
    @NotBlank
    private String email;

    @Past
    @NotNull
    private LocalDate birthDate;

}
