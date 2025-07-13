package com.example.userServiceTask.dto.cardInfo;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateCardInfoDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String number;

    @NotBlank(message = "Card holder is required")
    private String holder;

    @Future(message = "Must be future date")
    @NotNull
    private LocalDate expirationDate;
}
