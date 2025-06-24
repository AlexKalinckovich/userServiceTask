package com.example.userServiceTask.dto.cardInfo;

import lombok.Data;

@Data
public class CardInfoResponseDto {
    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private String expirationDate;
}
