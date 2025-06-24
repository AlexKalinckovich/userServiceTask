package com.example.userServiceTask.dto.user;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponseDto {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private LocalDate birthDate;

    private List<CardInfoResponseDto> cards;

}
