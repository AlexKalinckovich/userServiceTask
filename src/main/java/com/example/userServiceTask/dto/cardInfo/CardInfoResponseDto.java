package com.example.userServiceTask.dto.cardInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardInfoResponseDto {
    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
