package com.example.userServiceTask.service.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;

import java.util.List;

public interface CardInfoService {

    CardInfoResponseDto createCardInfo(final CardInfoCreateDto cardInfo);
    CardInfoResponseDto updateCardInfo(final CardInfoUpdateDto cardInfoUpdateDto);
    CardInfoResponseDto getCardInfoById(final Long id);
    CardInfoResponseDto deleteCardInfoById(final Long id);
    List<CardInfoResponseDto> getAllCards(final Long userId);
}
