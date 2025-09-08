package com.example.userServiceTask.dto.cardInfo;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CardInfoUpdateDto {
    @NotNull(message = "Card ID is required")
    private Long id;

    private Long userId;

    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String number;

    @Size(max = 100)
    private String holder;

    @Future
    @NotNull
    private LocalDate expirationDate;
}
