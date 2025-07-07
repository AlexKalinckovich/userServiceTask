package com.example.userServiceTask.service;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.repositories.CardInfoRepository;
import com.example.userServiceTask.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Autowired
    public CardInfoService(final CardInfoRepository cardInfoRepository,
                           final UserRepository userRepository,
                           final CardInfoMapper cardInfoMapper) {
        this.cardInfoRepository = cardInfoRepository;
        this.userRepository = userRepository;
        this.cardInfoMapper = cardInfoMapper;
    }

    @Transactional
    @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
    public CardInfoResponseDto createCardInfo(final CreateCardInfoDto cardInfo) {
        final Optional<User> cardUser = userRepository.findById(cardInfo.getUserId());
        if (cardUser.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        final CardInfo toSaveCardInfo = cardInfoMapper.fromCreateDto(cardInfo);

        toSaveCardInfo.setUser(cardUser.get());

        return cardInfoMapper.toResponseDto(
                cardInfoRepository.save(toSaveCardInfo)
        );
    }

    @Transactional
    @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
    public CardInfoResponseDto updateCardInfo(final UpdateCardInfoDto updateCardInfoDto) {
        final Long id = updateCardInfoDto.getId();
        final Optional<CardInfo> cardInfoOptional = cardInfoRepository.findById(id);
        if(cardInfoOptional.isEmpty()) {
            throw new EntityNotFoundException("CardInfo with id " + updateCardInfoDto.getId() + " does not exist");
        }

        final CardInfo originalCardInfo = cardInfoOptional.get();
        updateCardInfoByCardInfoDto(originalCardInfo, updateCardInfoDto);

        return cardInfoMapper.toResponseDto(originalCardInfo);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "CARD_INFO_CACHE", key = "#id")
    public CardInfoResponseDto getCardInfoById(final Long id) {
        final Optional<CardInfo> cardInfoOptional = cardInfoRepository.findById(id);
        if(cardInfoOptional.isEmpty()) {
            throw new EntityNotFoundException("CardInfo with id " + id + " does not exist");
        }

        return cardInfoMapper.toResponseDto(
                cardInfoOptional.get()
        );
    }


    @Transactional
    @CacheEvict(value = "CARD_INFO_CACHE", key = "#id")
    public int deleteCardInfoById(final Long id) {
        return cardInfoRepository.deleteCardInfoById(id);
    }

    private void updateCardInfoByCardInfoDto(final CardInfo cardInfo, final UpdateCardInfoDto updateDto) {
        final String holder = cardInfo.getHolder();

        if(holder != null && !holder.isBlank()) {
            cardInfo.setHolder(updateDto.getHolder());
        }

        final LocalDate currentValue = updateDto.getExpirationDate();
        if(currentValue != null){
            cardInfo.setExpirationDate(currentValue);
        }

        final String number = updateDto.getNumber();
        if(number != null && !number.isBlank()) {
            cardInfo.setNumber(number);
        }

        final Long userId = updateDto.getUserId();
        if(userId != null) {
            final Optional<User> userOptional = userRepository.findById(userId);
            userOptional.ifPresent(cardInfo::setUser);
        }
    }
}
