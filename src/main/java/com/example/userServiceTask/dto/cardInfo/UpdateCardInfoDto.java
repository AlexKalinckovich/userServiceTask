package com.example.userServiceTask.dto.cardInfo;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Data
public class UpdateCardInfoDto {
    @NotNull(message = "Card ID is required")
    @Range(min = 1)
    private Long id;

    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String number;

    @Size(max = 100)
    private String holder;

    @Future
    @NotNull
    private LocalDate expirationDate;
}
