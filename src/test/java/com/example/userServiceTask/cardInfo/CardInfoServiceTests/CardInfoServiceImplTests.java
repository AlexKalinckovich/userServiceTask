package com.example.userServiceTask.cardInfo.CardInfoServiceTests;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;
import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.cardInfo.CardInfo;
import com.example.userServiceTask.model.user.User;
import com.example.userServiceTask.repositories.CardInfoRepository;
import com.example.userServiceTask.repositories.user.UserRepository;
import com.example.userServiceTask.service.cardInfo.CardInfoServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableCaching
@ExtendWith(MockitoExtension.class)
public class CardInfoServiceImplTests extends AbstractContainerBaseTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CardInfoRepository cardInfoRepository;

    @MockitoBean
    private CardInfoMapper cardInfoMapper;

    @Autowired
    private CardInfoServiceImpl cardInfoServiceImpl;

    @Autowired
    private CacheManager cacheManager;

    private static final Long DEFAULT_ID = 1L;
    private static final Long NOT_EXISTING_ID = 999L;

    private static CardInfoCreateDto cardInfoCreateDto;
    private static CardInfoUpdateDto cardInfoUpdateDto;
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

        cardInfoCreateDto = CardInfoCreateDto.builder()
                .userId(DEFAULT_ID)
                .holder("Jane")
                .number("3dA34DC98EF45A24")
                .expirationDate(LocalDate.MAX)
                .build();

        cardInfoUpdateDto = CardInfoUpdateDto.builder()
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

        when(cardInfoMapper.fromCreateDto(any(CardInfoCreateDto.class)))
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
                assertDoesNotThrow(() -> cardInfoServiceImpl.createCardInfo(cardInfoCreateDto));

        assertEquals(cardInfoCreateDto.getHolder(), response.getHolder());
        assertEquals(cardInfoCreateDto.getNumber(), response.getNumber());
        verify(cardInfoRepository, times(1)).save(any());
    }

    @Test
    void updateCardInfo_success() {
        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        doAnswer(invocation -> {
            final CardInfoUpdateDto dto = invocation.getArgument(0);
            final CardInfo entity = invocation.getArgument(1);
            entity.setHolder(dto.getHolder());
            entity.setNumber(dto.getNumber());
            entity.setExpirationDate(dto.getExpirationDate());
            return null;
        }).when(cardInfoMapper).updateFromDto(any(CardInfoUpdateDto.class), any(CardInfo.class));

        when(cardInfoRepository.save(any(CardInfo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(cardInfoMapper.toResponseDto(any(CardInfo.class)))
                .thenAnswer(inv -> {
                    final CardInfo c = inv.getArgument(0);
                    return CardInfoResponseDto.builder()
                            .id(c.getId())
                            .holder(c.getHolder())
                            .number(c.getNumber())
                            .expirationDate(c.getExpirationDate())
                            .build();
                });

        final CardInfoResponseDto updated = cardInfoServiceImpl.updateCardInfo(cardInfoUpdateDto);

        assertNotNull(updated);
        assertEquals("Updated Holder", updated.getHolder());
        assertEquals("UpdatedNumber123", updated.getNumber());
        assertEquals(LocalDate.now(), updated.getExpirationDate());

        verify(cardInfoRepository).findById(DEFAULT_ID);
        verify(cardInfoMapper).updateFromDto(any(), any());
        verify(cardInfoRepository).save(any(CardInfo.class));
        verify(cardInfoMapper).toResponseDto(any(CardInfo.class));
    }

    @Test
    void updateCardInfo_notFound_throwsException() {
        when(cardInfoRepository.findById(NOT_EXISTING_ID))
                .thenReturn(Optional.empty());

        final CardInfoUpdateDto dto = CardInfoUpdateDto.builder()
                .id(NOT_EXISTING_ID)
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> cardInfoServiceImpl.updateCardInfo(dto));

        verify(cardInfoRepository, never()).save(any());
    }

    @Test
    void updateCardInfo_updatesCache() {
        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);
        cache.put(DEFAULT_ID, cardInfoMapper.toResponseDto(cardInfo));

        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        when(cardInfoRepository.save(any(CardInfo.class)))
                .thenAnswer(invocation -> {
                    final CardInfo c = invocation.getArgument(0);
                    c.setHolder(cardInfoUpdateDto.getHolder());
                    c.setNumber(cardInfoUpdateDto.getNumber());
                    c.setExpirationDate(cardInfoUpdateDto.getExpirationDate());
                    return c;
                });

        when(cardInfoMapper.toResponseDto(any(CardInfo.class)))
                .thenAnswer(inv -> {
                    final CardInfo c = inv.getArgument(0);
                    return CardInfoResponseDto.builder()
                            .id(c.getId())
                            .holder(c.getHolder())
                            .number(c.getNumber())
                            .expirationDate(c.getExpirationDate())
                            .build();
                });

        cardInfoServiceImpl.updateCardInfo(cardInfoUpdateDto);

        final CardInfoResponseDto cached = cache.get(DEFAULT_ID, CardInfoResponseDto.class);
        assertNotNull(cached);
        assertEquals("Updated Holder", cached.getHolder());
        assertEquals("UpdatedNumber123", cached.getNumber());
    }

    @Test
    void getCardInfoById_success() {
        when(cardInfoRepository.findById(DEFAULT_ID))
                .thenReturn(Optional.of(cardInfo));

        final CardInfoResponseDto response = cardInfoServiceImpl.getCardInfoById(DEFAULT_ID);

        assertEquals(cardInfo.getHolder(), response.getHolder());
        verify(cardInfoRepository, times(1)).findById(DEFAULT_ID);
    }

    @Test
    void deleteCardInfoById_success() {
        when(cardInfoRepository.existsById(DEFAULT_ID))
                .thenReturn(true);

        when(cardInfoRepository.deleteCardInfoById(DEFAULT_ID))
                .thenReturn(1);

        final CardInfoResponseDto result = cardInfoServiceImpl.deleteCardInfoById(DEFAULT_ID);

        assertEquals(cardInfoResponseDto, result);
        verify(cardInfoRepository, times(1)).existsById(DEFAULT_ID);
        verify(cardInfoRepository, times(1)).deleteCardInfoById(DEFAULT_ID);
    }

    @Test
    void deleteCardInfoById_notFound_throwsException() {
        when(cardInfoRepository.existsById(NOT_EXISTING_ID))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> cardInfoServiceImpl.deleteCardInfoById(NOT_EXISTING_ID));

        verify(cardInfoRepository, never()).deleteCardInfoById(anyLong());
    }

    @Test
    void deleteCardInfoById_evictsFromCache() {
        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);
        cache.put(DEFAULT_ID, cardInfoMapper.toResponseDto(cardInfo));

        when(cardInfoRepository.existsById(DEFAULT_ID))
                .thenReturn(true);
        when(cardInfoRepository.deleteCardInfoById(DEFAULT_ID))
                .thenReturn(1);

        cardInfoServiceImpl.deleteCardInfoById(DEFAULT_ID);

        assertNull(cache.get(DEFAULT_ID));
    }

    @Test
    void createCardInfo_cachesResult() {
        when(cardInfoRepository.save(any(CardInfo.class)))
                .thenReturn(cardInfo);

        final CardInfoResponseDto response = cardInfoServiceImpl.createCardInfo(cardInfoCreateDto);

        final Cache cache = cacheManager.getCache("CARD_INFO_CACHE");
        assertNotNull(cache);

        final CardInfoResponseDto cached = cache.get(response.getId(), CardInfoResponseDto.class);
        assertNotNull(cached);
        assertEquals(response.getId(), cached.getId());
        assertEquals(response.getHolder(), cached.getHolder());
    }
}
