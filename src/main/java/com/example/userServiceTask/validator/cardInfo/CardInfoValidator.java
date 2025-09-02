package com.example.userServiceTask.validator.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;
import com.example.userServiceTask.exception.cardInfo.CardAlreadyExistsException;
import com.example.userServiceTask.exception.cardInfo.CardNotFoundException;
import com.example.userServiceTask.exception.cardInfo.CardOwnershipException;
import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.cardInfo.CardInfo;
import com.example.userServiceTask.model.user.User;
import com.example.userServiceTask.repositories.CardInfoRepository;
import com.example.userServiceTask.validator.Validator;
import com.example.userServiceTask.validator.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardInfoValidator implements Validator<CardInfoCreateDto, CardInfoUpdateDto, CardInfo> {

    private final CardInfoMapper cardInfoMapper;
    private final UserValidator userValidator;
    private final CardInfoRepository cardInfoRepository;

    @Override
    public CardInfo validateCreateDto(final @NotNull CardInfoCreateDto createDto) {
        final User cardUser = userValidator.checkUserForExistenceById(createDto.getUserId());
        final String number  = createDto.getNumber();
        if(cardInfoRepository.existsByNumber(number)) {
            throw new CardAlreadyExistsException(number);
        }

        final CardInfo toSaveCardInfo = cardInfoMapper.fromCreateDto(createDto);
        checkHolderWithUsername(cardUser.getName(),toSaveCardInfo.getHolder());
        toSaveCardInfo.setUser(cardUser);
        return toSaveCardInfo;
    }

    @Override
    public CardInfo validateUpdateDto(final @NotNull CardInfoUpdateDto updateDto) {
        final Long id = updateDto.getId();
        if(cardInfoRepository.existsByNumber(updateDto.getNumber())) {
            throw new CardAlreadyExistsException(updateDto.getNumber());
        }

        return checkCardInfoForExistenceById(id);
    }

    public void checkHolderWithUsername(final @NotNull String username, final String holder) {
        if(!username.equals(holder)){
            throw new CardOwnershipException("Username and holder difference");
        }
    }

    public CardInfo checkCardInfoForExistenceById(final Long id){
        return  cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
    }


}
