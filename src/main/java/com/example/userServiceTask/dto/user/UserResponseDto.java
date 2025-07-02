package com.example.userServiceTask.dto.user;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private LocalDate birthDate;

    private List<CardInfoResponseDto> cards;

}
