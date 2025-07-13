    package com.example.userServiceTask.service.cardInfo;

    import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
    import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
    import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
    import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
    import com.example.userServiceTask.model.CardInfo;
    import com.example.userServiceTask.model.user.User;
    import com.example.userServiceTask.repositories.CardInfoRepository;
    import com.example.userServiceTask.repositories.user.UserRepository;
    import jakarta.persistence.EntityNotFoundException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.cache.annotation.CacheEvict;
    import org.springframework.cache.annotation.CachePut;
    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

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
            final User cardUser = userRepository.findById(cardInfo.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            final CardInfo toSaveCardInfo = cardInfoMapper.fromCreateDto(cardInfo);

            toSaveCardInfo.setUser(cardUser);

            return cardInfoMapper.toResponseDto(
                    cardInfoRepository.save(toSaveCardInfo)
            );
        }

        @Transactional
        @CachePut(value = "CARD_INFO_CACHE", key = "#result.id")
        public CardInfoResponseDto updateCardInfo(final UpdateCardInfoDto updateCardInfoDto) {
            final Long id = updateCardInfoDto.getId();
            final CardInfo cardInfo = cardInfoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Card not found"));

            cardInfoMapper.updateFromDto(updateCardInfoDto, cardInfo);

            return cardInfoMapper.toResponseDto(cardInfoRepository.save(cardInfo));
        }

        @Transactional(readOnly = true)
        @Cacheable(value = "CARD_INFO_CACHE", key = "#id")
        public CardInfoResponseDto getCardInfoById(final Long id) {
            final CardInfo cardInfoOptional = cardInfoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Card not found"));

            return cardInfoMapper.toResponseDto(cardInfoOptional);
        }


        @Transactional
        @CacheEvict(value = "CARD_INFO_CACHE", key = "#id")
        public int deleteCardInfoById(final Long id) {

            if(!cardInfoRepository.existsById(id)) {
                throw new EntityNotFoundException("Card not found");
            }

            return cardInfoRepository.deleteCardInfoById(id);
        }

    }
