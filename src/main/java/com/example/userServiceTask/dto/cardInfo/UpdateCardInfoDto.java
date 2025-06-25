package com.example.userServiceTask.dto.cardInfo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateCardInfoDto {
    @NotNull(message = "Card ID is required")
    private Long id;

    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String number;

    private String holder;

    @Pattern(
            regexp = "(0[1-9]|1[0-2])/[0-9]{2}",
            message = "Expiration date format must be MM/YY"
    )
    private String expirationDate;
}
