package com.example.userServiceTask.cardInfo.CardInfoControllerTests;

import com.example.userServiceTask.controller.cardInfo.CardInfoController;
import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.service.CardInfoService;
import com.example.userServiceTask.utils.AbstractContainerBaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardInfoController.class)
public class CardInfoControllerTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardInfoService cardInfoService;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private CreateCardInfoDto createCardInfoDto;
    private UpdateCardInfoDto updateCardInfoDto;
    private CardInfoResponseDto cardInfoResponseDto;

    @BeforeEach
    void setUp() {
        createCardInfoDto = CreateCardInfoDto.builder()
                .userId(VALID_ID)
                .holder("Jane")
                .number("7865748596748576")
                .expirationDate(LocalDate.MAX)
                .build();

        updateCardInfoDto = UpdateCardInfoDto.builder()
                .id(VALID_ID)
                .holder("Updated Holder")
                .number("7865748596748576")
                .expirationDate(LocalDate.MAX)
                .build();

        cardInfoResponseDto = CardInfoResponseDto.builder()
                .id(VALID_ID)
                .holder("Jane")
                .number("1234325897657865")
                .expirationDate(LocalDate.MAX)
                .build();
    }


    @Test
    void createCardInfo_ValidRequest_ReturnsCreated() throws Exception {
        when(cardInfoService.createCardInfo(any(CreateCardInfoDto.class)))
                .thenReturn(CardInfoResponseDto.builder()
                        .id(VALID_ID)
                        .holder("Jane")
                        .number("7865748596748576")
                        .expirationDate(LocalDate.MAX)
                        .build());

        mockMvc.perform(post("/cardInfo/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardInfoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.holder").value(createCardInfoDto.getHolder()))
                .andExpect(jsonPath("$.number").value(createCardInfoDto.getNumber()));

        verify(cardInfoService, times(1)).createCardInfo(any());
    }

    @Test
    void getCardInfo_ExistingId_ReturnsOk() throws Exception {
        when(cardInfoService.getCardInfoById(VALID_ID))
                .thenReturn(cardInfoResponseDto);

        mockMvc.perform(get("/cardInfo/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.holder").value(cardInfoResponseDto.getHolder()));

        verify(cardInfoService, times(1)).getCardInfoById(VALID_ID);
    }

    @Test
    void updateCardInfo_ValidRequest_ReturnsOk() throws Exception {
        when(cardInfoService.updateCardInfo(any(UpdateCardInfoDto.class)))
                .thenReturn(CardInfoResponseDto.builder()
                        .id(VALID_ID)
                        .holder("Updated Holder")
                        .number("7865748596748576")
                        .expirationDate(LocalDate.MAX)
                        .build());

        mockMvc.perform(put("/cardInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCardInfoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value(updateCardInfoDto.getHolder()))
                .andExpect(jsonPath("$.number").value(updateCardInfoDto.getNumber()));

        verify(cardInfoService, times(1)).updateCardInfo(any());
    }

    @Test
    void deleteCardInfo_ExistingId_ReturnsOk() throws Exception {
        when(cardInfoService.deleteCardInfoById(VALID_ID))
                .thenReturn(1);

        mockMvc.perform(delete("/cardInfo/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(cardInfoService, times(1)).deleteCardInfoById(VALID_ID);
    }


    @Test
    void getCardInfo_NotExistingId_ReturnsNotFound() throws Exception {
        when(cardInfoService.getCardInfoById(INVALID_ID))
                .thenThrow(new EntityNotFoundException("CardInfo not found"));

        mockMvc.perform(get("/cardInfo/{id}", INVALID_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));

        verify(cardInfoService, times(1)).getCardInfoById(INVALID_ID);
    }

    @Test
    void createCardInfo_InvalidRequest_ReturnsBadRequest() throws Exception {
        final CreateCardInfoDto invalidDto = CreateCardInfoDto.builder().build();

        mockMvc.perform(post("/cardInfo/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.holder").value("Card holder is required"))
                .andExpect(jsonPath("$.details.number").value("Card number is required"))
                .andExpect(jsonPath("$.details.userId").value("User ID is required"));

        verify(cardInfoService, never()).createCardInfo(any());
    }

    @Test
    void updateCardInfo_NotExistingId_ReturnsNotFound() throws Exception {
        final UpdateCardInfoDto invalidDto = UpdateCardInfoDto.builder()
                        .id(INVALID_ID).expirationDate(LocalDate.MAX).build();

        when(cardInfoService.updateCardInfo(any()))
                .thenThrow(new EntityNotFoundException("CardInfo not found"));

        mockMvc.perform(put("/cardInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details.error").value("CardInfo not found"));

        verify(cardInfoService, times(1)).updateCardInfo(any());
    }

    @Test
    void createCardInfo_InvalidExpirationDate_ReturnsBadRequest() throws Exception {
        final CreateCardInfoDto expiredCard = CreateCardInfoDto.builder()
                .userId(VALID_ID)
                .number("random")
                .holder("random")
                .expirationDate(LocalDate.MIN)
                .build();

        mockMvc.perform(post("/cardInfo/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expiredCard)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.expirationDate").value("Must be future date"));

        verify(cardInfoService, never()).createCardInfo(any());
    }

    @Test
    void updateCardInfo_MissingId_ReturnsBadRequest() throws Exception {
        final UpdateCardInfoDto noIdDto = UpdateCardInfoDto.builder().id(null).build();

        mockMvc.perform(put("/cardInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noIdDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.id").value("Card ID is required"));

        verify(cardInfoService, never()).updateCardInfo(any());
    }
}
