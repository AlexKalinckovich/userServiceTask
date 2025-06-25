package com.example.userServiceTask.dto.cardInfo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoResponseDto {
    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
