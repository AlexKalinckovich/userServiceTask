package com.example.userServiceTask.service.cardInfo;

    import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
    import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
    import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;
    import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
    import com.example.userServiceTask.model.cardInfo.CardInfo;
    import com.example.userServiceTask.repositories.CardInfoRepository;
    import com.example.userServiceTask.validator.cardInfo.CardInfoValidator;
    import com.example.userServiceTask.validator.user.UserValidator;
    import lombok.RequiredArgsConstructor;
    import org.springframework.cache.annotation.CacheEvict;
    import org.springframework.cache.annotation.CachePut;
    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class CardInfoServiceImpl implements CardInfoService {

        private final CardInfoRepository cardInfoRepository;
        private final CardInfoMapper cardInfoMapper;
        private final CardInfoValidator cardInfoValidator;
        private final UserValidator userValidator;

        @Transactional
        @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
        public CardInfoResponseDto createCardInfo(final CardInfoCreateDto cardInfo) {
            final CardInfo toSaveCardInfo = cardInfoValidator.validateCreateDto(cardInfo);
            return cardInfoMapper.toResponseDto(
                    cardInfoRepository.save(toSaveCardInfo)
            );
        }

        @Transactional
        @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
        public CardInfoResponseDto updateCardInfo(final CardInfoUpdateDto cardInfoUpdateDto) {
            final CardInfo cardInfo = cardInfoValidator.validateUpdateDto(cardInfoUpdateDto);
            cardInfoMapper.updateFromDto(cardInfoUpdateDto, cardInfo);
            return cardInfoMapper.toResponseDto(cardInfoRepository.save(cardInfo));
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "CARD_INFO_CACHE", key = "#id")
        public CardInfoResponseDto getCardInfoById(final Long id) {
            final CardInfo cardInfo = cardInfoValidator.checkCardInfoForExistenceById(id);

            return cardInfoMapper.toResponseDto(cardInfo);
        }


        @Transactional
        @CacheEvict(value = "CARD_INFO_CACHE", key = "#id")
        public CardInfoResponseDto deleteCardInfoById(final Long id) {
            final CardInfo cardInfo = cardInfoValidator.checkCardInfoForExistenceById(id);
            cardInfoRepository.deleteCardInfoById(id);
            return cardInfoMapper.toResponseDto(cardInfo);
        }

        public List<CardInfoResponseDto> getAllCards(final Long userId) {
            userValidator.checkUserForExistenceById(userId);
            final List<CardInfo> cardInfos = cardInfoRepository.getCardInfosByUserId(userId);

            return cardInfoMapper.toResponseDtoList(cardInfos);
        }
    }
