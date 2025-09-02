package com.example.userServiceTask.cardInfo.CardInfoControllerTests;

import com.example.userServiceTask.config.MessageConfig;
import com.example.userServiceTask.controller.cardInfo.CardInfoController;
import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;
import com.example.userServiceTask.exception.response.ExceptionResponseService;
import com.example.userServiceTask.messageConstants.ErrorMessage;
import com.example.userServiceTask.service.cardInfo.CardInfoServiceImpl;
import com.example.userServiceTask.service.messages.MessageService;
import com.example.userServiceTask.utils.AbstractContainerBaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
@Import({MessageService.class, ExceptionResponseService.class, MessageConfig.class})
public class CardInfoControllerTests extends AbstractContainerBaseTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardInfoServiceImpl cardInfoServiceImpl;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private CardInfoCreateDto cardInfoCreateDto;
    private CardInfoUpdateDto cardInfoUpdateDto;
    private CardInfoResponseDto cardInfoResponseDto;

    @BeforeEach
    void setUp() {
        cardInfoCreateDto = CardInfoCreateDto.builder()
                .userId(VALID_ID)
                .holder("Jane")
                .number("7865748596748576")
                .expirationDate(LocalDate.MAX)
                .build();

        cardInfoUpdateDto = CardInfoUpdateDto.builder()
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
        when(cardInfoServiceImpl.createCardInfo(any(CardInfoCreateDto.class)))
                .thenReturn(CardInfoResponseDto.builder()
                        .id(VALID_ID)
                        .holder("Jane")
                        .number("7865748596748576")
                        .expirationDate(LocalDate.MAX)
                        .build());

        mockMvc.perform(post("/cardInfo/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.holder").value(cardInfoCreateDto.getHolder()))
                .andExpect(jsonPath("$.number").value(cardInfoCreateDto.getNumber()));

        verify(cardInfoServiceImpl, times(1)).createCardInfo(any());
    }

    @Test
    void getCardInfo_ExistingId_ReturnsOk() throws Exception {
        when(cardInfoServiceImpl.getCardInfoById(VALID_ID))
                .thenReturn(cardInfoResponseDto);

        mockMvc.perform(get("/cardInfo/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.holder").value(cardInfoResponseDto.getHolder()));

        verify(cardInfoServiceImpl, times(1)).getCardInfoById(VALID_ID);
    }

    @Test
    void updateCardInfo_ValidRequest_ReturnsOk() throws Exception {
        when(cardInfoServiceImpl.updateCardInfo(any(CardInfoUpdateDto.class)))
                .thenReturn(CardInfoResponseDto.builder()
                        .id(VALID_ID)
                        .holder("Updated Holder")
                        .number("7865748596748576")
                        .expirationDate(LocalDate.MAX)
                        .build());

        mockMvc.perform(put("/cardInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInfoUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value(cardInfoUpdateDto.getHolder()))
                .andExpect(jsonPath("$.number").value(cardInfoUpdateDto.getNumber()));

        verify(cardInfoServiceImpl, times(1)).updateCardInfo(any());
    }

    @Test
    void deleteCardInfo_ExistingId_ReturnsOk() throws Exception {
        when(cardInfoServiceImpl.deleteCardInfoById(VALID_ID))
                .thenReturn(cardInfoResponseDto);

        mockMvc.perform(delete("/cardInfo/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(cardInfoServiceImpl, times(1)).deleteCardInfoById(VALID_ID);
    }


    @Test
    void getCardInfo_NotExistingId_ReturnsNotFound() throws Exception {
        when(cardInfoServiceImpl.getCardInfoById(INVALID_ID))
                .thenThrow(new EntityNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.getKey()));

        mockMvc.perform(get("/cardInfo/{id}", INVALID_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details.Error").value(ErrorMessage.RESOURCE_NOT_FOUND.getKey()));

        verify(cardInfoServiceImpl, times(1)).getCardInfoById(INVALID_ID);
    }

    @Test
    void createCardInfo_InvalidRequest_ReturnsBadRequest() throws Exception {
        final CardInfoCreateDto invalidDto = CardInfoCreateDto.builder().build();

        mockMvc.perform(post("/cardInfo/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.holder").value("Card holder is required"))
                .andExpect(jsonPath("$.details.number").value("Card number is required"))
                .andExpect(jsonPath("$.details.userId").value("User ID is required"));

        verify(cardInfoServiceImpl, never()).createCardInfo(any());
    }

    @Test
    void updateCardInfo_NotExistingId_ReturnsNotFound() throws Exception {
        final CardInfoUpdateDto invalidDto = CardInfoUpdateDto.builder()
                        .id(INVALID_ID).expirationDate(LocalDate.MAX).build();

        when(cardInfoServiceImpl.updateCardInfo(any()))
                .thenThrow(new EntityNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.getKey()));

        mockMvc.perform(put("/cardInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details.Error").value(ErrorMessage.RESOURCE_NOT_FOUND.getKey()));

        verify(cardInfoServiceImpl, times(1)).updateCardInfo(any());
    }

    @Test
    void createCardInfo_InvalidExpirationDate_ReturnsBadRequest() throws Exception {
        final CardInfoCreateDto expiredCard = CardInfoCreateDto.builder()
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

        verify(cardInfoServiceImpl, never()).createCardInfo(any());
    }

    @Test
    void updateCardInfo_MissingId_ReturnsBadRequest() throws Exception {
        final CardInfoUpdateDto noIdDto = CardInfoUpdateDto.builder().id(null).build();

        mockMvc.perform(put("/cardInfo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noIdDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.id").value("Card ID is required"));

        verify(cardInfoServiceImpl, never()).updateCardInfo(any());
    }
}
