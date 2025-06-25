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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;


    @Autowired
    public CardInfoService(final CardInfoRepository cardInfoRepository,
                           final CardInfoMapper cardInfoMapper,
                           final UserRepository userRepository) {
        this.cardInfoRepository = cardInfoRepository;
        this.cardInfoMapper = cardInfoMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public CardInfoResponseDto createCardInfo(final CreateCardInfoDto createCardInfoDto) {
        final Optional<User> user = userRepository.findById(createCardInfoDto.getUserId());

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User with id " + createCardInfoDto.getUserId() + " does not exist");
        }

        final CardInfo cardInfo = cardInfoMapper.fromCreateDto(createCardInfoDto);
        cardInfo.setUser(user.get());

        final CardInfo savedCardInfo = cardInfoRepository.save(cardInfo);

        return cardInfoMapper.toResponseDto(savedCardInfo);
    }

    @Transactional
    public CardInfoResponseDto updateCardInfo(final UpdateCardInfoDto updateCardInfoDto) {
        final Long id = updateCardInfoDto.getId();
        final Optional<CardInfo> cardInfoOptional = cardInfoRepository.findById(id);
        if(cardInfoOptional.isEmpty()) {
            throw new EntityNotFoundException("CardInfo with id " + updateCardInfoDto.getId() + " does not exist");
        }
        final CardInfo cardInfo = cardInfoOptional.get();

        cardInfoMapper.updateFromDto(updateCardInfoDto, cardInfo);

        return cardInfoMapper.toResponseDto(cardInfo);
    }

    @Transactional(readOnly = true)
    public CardInfoResponseDto getCardInfoById(final Long id) {
        final Optional<CardInfo> cardInfoOptional = cardInfoRepository.findById(id);
        if(cardInfoOptional.isEmpty()) {
            throw new EntityNotFoundException("CardInfo with id " + id + " does not exist");
        }

        return cardInfoMapper.toResponseDto(cardInfoOptional.get());
    }


    @Transactional
    public CardInfoResponseDto deleteCardInfoById(final Long id) {
        return cardInfoMapper.toResponseDto(cardInfoRepository.deleteCardInfoById(id));
    }
}
