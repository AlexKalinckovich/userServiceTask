package com.example.userServiceTask.controller.cardInfo;

import com.example.dto.cardInfo.CardInfoResponseDto;
import com.example.dto.cardInfo.CreateCardInfoDto;
import com.example.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.service.cardInfo.CardInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(cardInfoService.updateCardInfo(updateCardInfoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteCardInfo(@PathVariable("id") Long id){
        return ResponseEntity.ok(cardInfoService.deleteCardInfoById(id));
    }
}
