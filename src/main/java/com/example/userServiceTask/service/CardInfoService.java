package com.example.userServiceTask.service;

import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.repositories.CardInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserService userService;


    @Autowired
    public CardInfoService(final CardInfoRepository cardInfoRepository,
                           final UserService userService) {
        this.cardInfoRepository = cardInfoRepository;
        this.userService = userService;
    }

    @Transactional
    @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
    public CardInfo createCardInfo(final CardInfo cardInfo) {
        final Optional<User> cardUser = Optional.ofNullable(cardInfo.getUser());
        final Long userId = cardUser.map(User::getId).orElse(null);

        final User user = userService.findUserById(userId);
        cardInfo.setUser(user);

        return cardInfoRepository.save(cardInfo);
    }

    @Transactional
    @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
    public CardInfo updateCardInfo(final UpdateCardInfoDto updateCardInfoDto) {
        final Long id = updateCardInfoDto.getId();
        final Optional<CardInfo> cardInfoOptional = cardInfoRepository.findById(id);
        if(cardInfoOptional.isEmpty()) {
            throw new EntityNotFoundException("CardInfo with id " + updateCardInfoDto.getId() + " does not exist");
        }

        return cardInfoRepository.updateCardInfoById(id, cardInfoOptional.get());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "CARD_INFO_CACHE", key = "#id")
    public CardInfo getCardInfoById(final Long id) {
        final Optional<CardInfo> cardInfoOptional = cardInfoRepository.findById(id);
        if(cardInfoOptional.isEmpty()) {
            throw new EntityNotFoundException("CardInfo with id " + id + " does not exist");
        }

        return cardInfoOptional.get();
    }


    @Transactional
    @CacheEvict(value = "CARD_INFO_CACHE", key = "#id")
    public CardInfo deleteCardInfoById(final Long id) {
        return cardInfoRepository.deleteCardInfoById(id);
    }
}
