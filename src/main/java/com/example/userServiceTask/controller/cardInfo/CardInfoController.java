package com.example.userServiceTask.controller.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
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

    @Autowired
    public CardInfoController(final CardInfoService cardInfoService) {
        this.cardInfoService = cardInfoService;
    }

    @PostMapping("/create")
    public ResponseEntity<CardInfoResponseDto> createCardInfo(@RequestBody @Valid CreateCardInfoDto createCardInfoDto){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoService.createCardInfo(createCardInfoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> getCardInfo(@PathVariable("id") Long id){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoService.getCardInfoById(id);
        return ResponseEntity.ok(cardInfoResponseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<CardInfoResponseDto> updateCardInfo(@RequestBody @Valid UpdateCardInfoDto updateCardInfoDto){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoService.updateCardInfo(updateCardInfoDto);
        return ResponseEntity.ok(cardInfoResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> deleteCardInfo(@PathVariable("id") Long id){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoService.deleteCardInfoById(id);
        return ResponseEntity.ok(cardInfoResponseDto);
    }
}
