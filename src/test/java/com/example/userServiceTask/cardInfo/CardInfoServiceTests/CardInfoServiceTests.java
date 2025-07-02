package com.example.userServiceTask.cardInfo.CardInfoServiceTests;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import com.example.userServiceTask.repositories.CardInfoRepository;
import com.example.userServiceTask.repositories.UserRepository;
import com.example.userServiceTask.service.CardInfoService;
import com.example.userServiceTask.utils.AbstractContainerBaseTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableCaching
@ExtendWith(MockitoExtension.class)
public class CardInfoServiceTests extends AbstractContainerBaseTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CardInfoRepository cardInfoRepository;

    @MockitoBean
    private CardInfoMapper cardInfoMapper;

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private CacheManager cacheManager;



    private static final Long DEFAULT_ID = 1L;
    private static final Long NOT_EXISTING_ID = 999L;

    private static CreateCardInfoDto createCardInfoDto;
    private static UpdateCardInfoDto updateCardInfoDto;
    private static User user;
    private static CardInfo cardInfo;
    private static CardInfoResponseDto cardInfoResponseDto;


    @BeforeAll
    static void setUpBeforeClass() {
        user = User.builder()
                .id(DEFAULT_ID)
                .name("John")
                .surname("Johnson")
                .email("johnjohn@gmail.com")
                .birthDate(LocalDate.MAX)
                .build();

        createCardInfoDto = CreateCardInfoDto.builder()
                .userId(DEFAULT_ID)
                .holder("Jane")
                .number("3dA34DC98EF45A24")
                .expirationDate(LocalDate.MAX)
                .build();

        updateCardInfoDto = UpdateCardInfoDto.builder()
                .id(DEFAULT_ID)
                .holder("Updated Holder")
                .number("UpdatedNumber123")
                .expirationDate(LocalDate.now())
                .build();

        cardInfo = CardInfo.builder()
                .id(DEFAULT_ID)
                .holder("Jane")
                .number("3dA34DC98EF45A24")
                .expirationDate(LocalDate.MAX)
                .user(user)
                .build();

        cardInfoResponseDto = CardInfoResponseDto.builder()
                .id(DEFAULT_ID)
                .holder("Jane")
                .number("3dA34DC98EF45A24")
                .expirationDate(LocalDate.MAX)
                .build();
    }

    @BeforeEach
    void setUp() {
        clearCache();

        Mockito.reset(cardInfoRepository);

        doReturn(Optional.of(user))
                .when(userRepository)
                        .findById(DEFAULT_ID);

        doReturn(Optional.empty())
                .when(userRepository)
                .findById(NOT_EXISTING_ID);

        when(cardInfoMapper.toResponseDto(any(CardInfo.class)))
                .thenReturn(cardInfoResponseDto);

        when(cardInfoMapper.fromCreateDto(any(CreateCardInfoDto.class)))
                .thenReturn(cardInfo);

    }

    private void clearCache() {
        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        if (cache != null){
            cache.clear();
        }
    }

    @Test
    void createCardInfo_success() {
        when(cardInfoRepository.save(any(CardInfo.class)))
                .thenReturn(cardInfo);

        final CardInfoResponseDto response =
                assertDoesNotThrow(() -> cardInfoService.createCardInfo(createCardInfoDto));

        assertEquals(createCardInfoDto.getHolder(), response.getHolder());
        assertEquals(createCardInfoDto.getNumber(), response.getNumber());
        verify(cardInfoRepository, times(1)).save(any());
    }

    @Test
    void createCardInfo_userNotFound_throwsEntityNotFoundException() {
        final CreateCardInfoDto dto = CreateCardInfoDto.builder()
                .userId(NOT_EXISTING_ID)
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> cardInfoService.createCardInfo(dto));
    }

    @Test
    void createCardInfo_cachesResult() {
        when(cardInfoRepository.save(any(CardInfo.class)))
                .thenReturn(cardInfo);

        final CardInfoResponseDto response = cardInfoService.createCardInfo(createCardInfoDto);

        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);
        assertNotNull(cache.get(response.getId()));

        final Cache.ValueWrapper r = cache.get(response.getId());

        assertNotNull(r);
        assertEquals(response, r.get());
    }

    @Test
    void updateCardInfo_success() {
        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        when(cardInfoRepository.updateCardInfoById(eq(DEFAULT_ID), any(CardInfo.class)))
                .thenReturn(1);

        when(cardInfoMapper.toResponseDto(any(CardInfo.class)))
                .thenReturn(
                        CardInfoResponseDto.builder()
                                .number(updateCardInfoDto.getNumber())
                                .id(updateCardInfoDto.getId())
                                .holder(updateCardInfoDto.getHolder())
                                .expirationDate(updateCardInfoDto.getExpirationDate())
                                .build()
                );

        final CardInfoResponseDto updated = assertDoesNotThrow(
                () -> cardInfoService.updateCardInfo(updateCardInfoDto)
        );

        assertNotNull(updated);
        assertEquals(updated.getNumber(), updateCardInfoDto.getNumber());
        assertEquals(updated.getHolder(), updateCardInfoDto.getHolder());
        assertEquals(updated.getExpirationDate(), updateCardInfoDto.getExpirationDate());
    }

    @Test
    void updateCardInfo_notFound_throwsException() {
        when(cardInfoRepository.findById(NOT_EXISTING_ID))
                .thenReturn(Optional.empty());

        final UpdateCardInfoDto dto = UpdateCardInfoDto.builder()
                .id(NOT_EXISTING_ID)
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> cardInfoService.updateCardInfo(dto));
    }

    @Test
    void updateCardInfo_updatesCache() {
        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);
        cache.put(DEFAULT_ID, cardInfoMapper.toResponseDto(cardInfo));

        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        when(cardInfoMapper.toResponseDto(any(CardInfo.class)))
                .thenReturn(
                        CardInfoResponseDto.builder()
                                .number(updateCardInfoDto.getNumber())
                                .id(updateCardInfoDto.getId())
                                .holder(updateCardInfoDto.getHolder())
                                .expirationDate(updateCardInfoDto.getExpirationDate())
                                .build()
                );

        when(cardInfoRepository.updateCardInfoById(eq(DEFAULT_ID), any(CardInfo.class)))
                .thenReturn(1)
                .thenAnswer(inv -> {
                    cardInfo.setHolder("Updated Holder");
                    return 1;
                });



        cardInfoService.updateCardInfo(updateCardInfoDto);

        final CardInfoResponseDto cached = cache.get(DEFAULT_ID, CardInfoResponseDto.class);
        assertNotNull(cached);
        assertEquals("Updated Holder", cached.getHolder());
    }

    @Test
    void getCardInfoById_success() {
        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        final CardInfoResponseDto response =
                assertDoesNotThrow(() -> cardInfoService.getCardInfoById(DEFAULT_ID));

        assertEquals(cardInfo.getHolder(), response.getHolder());
        verify(cardInfoRepository, times(1)).findById(DEFAULT_ID);
    }

    @Test
    void getCardInfoById_notFound_throwsException() {
        when(cardInfoRepository.findById(NOT_EXISTING_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> cardInfoService.getCardInfoById(NOT_EXISTING_ID));
    }

    @Test
    void getCardInfoById_cachesResult() {
        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        final CardInfoResponseDto response = cardInfoService.getCardInfoById(DEFAULT_ID);

        final CardInfoResponseDto cachedResponse = cardInfoService.getCardInfoById(DEFAULT_ID);
        verify(cardInfoRepository, times(1)).findById(DEFAULT_ID);

        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);

        final Cache.ValueWrapper cachedValue = cache.get(DEFAULT_ID);
        assertNotNull(cachedValue);
        assertEquals(response, cachedValue.get());
        assertEquals(response, cachedResponse);
    }

    @Test
    void deleteCardInfoById_notFound_returnsZero() {
        final long nonExistingId = NOT_EXISTING_ID;

        Mockito.doReturn(0)
                .when(cardInfoRepository)
                .deleteCardInfoById(nonExistingId);

        final int result = cardInfoService.deleteCardInfoById(nonExistingId);

        assertEquals(0, result);
        verify(cardInfoRepository, times(1)).deleteCardInfoById(nonExistingId);
    }

    @Test
    void deleteCardInfoById_success() {
        final long existingId = 1L;

        Mockito.doReturn(1)
                .when(cardInfoRepository)
                .deleteCardInfoById(existingId);

        final int result = cardInfoService.deleteCardInfoById(existingId);

        assertEquals(1, result);
        verify(cardInfoRepository, times(1)).deleteCardInfoById(existingId);

        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);
        assertNull(cache.get(existingId));
    }

    @Test
    void deleteCardInfoById_evictsFromCache() {
        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);

        cache.put(DEFAULT_ID, cardInfoMapper.toResponseDto(cardInfo));

        when(cardInfoRepository.deleteCardInfoById(DEFAULT_ID))
                .thenReturn(1);

        cardInfoService.deleteCardInfoById(DEFAULT_ID);

        assertNull(cache.get(DEFAULT_ID));
    }
}
