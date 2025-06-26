package com.example.userServiceTask.controller.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.service.CardInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cardInfo")
public class CardInfoController {

    private final CardInfoService cardInfoService;
    private final CardInfoMapper cardInfoMapper;

    @Autowired
    public CardInfoController(final CardInfoService cardInfoService,
                              final CardInfoMapper cardInfoMapper) {
        this.cardInfoService = cardInfoService;
        this.cardInfoMapper = cardInfoMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<CardInfoResponseDto> createCardInfo(@RequestBody @Valid CreateCardInfoDto createCardInfoDto){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoMapper.toResponseDto(
                cardInfoService.createCardInfo(
                        cardInfoMapper.fromCreateDto(createCardInfoDto)
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> getCardInfo(@PathVariable("id") Long id){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoMapper.toResponseDto(
                cardInfoService.getCardInfoById(id)
        );
        return ResponseEntity.ok(cardInfoResponseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<CardInfoResponseDto> updateCardInfo(@RequestBody @Valid UpdateCardInfoDto updateCardInfoDto){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoMapper.toResponseDto(
                cardInfoService.updateCardInfo(updateCardInfoDto)
        );
        return ResponseEntity.ok(cardInfoResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> deleteCardInfo(@PathVariable("id") Long id){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoMapper.toResponseDto(
                cardInfoService.deleteCardInfoById(id)
        );
        return ResponseEntity.ok(cardInfoResponseDto);
    }
}
