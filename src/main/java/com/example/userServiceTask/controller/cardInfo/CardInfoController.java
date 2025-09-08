package com.example.userServiceTask.controller.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;
import com.example.userServiceTask.service.cardInfo.CardInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/cardInfo")
@RequiredArgsConstructor
public class CardInfoController {
    private final CardInfoService cardInfoServiceImpl;


    @PostMapping("/create")
    public ResponseEntity<CardInfoResponseDto> createCardInfo(@RequestBody @Valid CardInfoCreateDto cardInfoCreateDto){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoServiceImpl.createCardInfo(cardInfoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> getCardInfo(@PathVariable("id") Long id){
        final CardInfoResponseDto cardInfoResponseDto = cardInfoServiceImpl.getCardInfoById(id);
        return ResponseEntity.ok(cardInfoResponseDto);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<CardInfoResponseDto>> getAllCardInfo(@PathVariable Long userId){
        final List<CardInfoResponseDto> cardInfoResponseDtos = cardInfoServiceImpl.getAllCards(userId);
        return ResponseEntity.ok(cardInfoResponseDtos);
    }

    @PutMapping("/update")
    public ResponseEntity<CardInfoResponseDto> updateCardInfo(@RequestBody @Valid CardInfoUpdateDto cardInfoUpdateDto){
        return ResponseEntity.ok(cardInfoServiceImpl.updateCardInfo(cardInfoUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> deleteCardInfo(@PathVariable("id") Long id){
        return ResponseEntity.ok(cardInfoServiceImpl.deleteCardInfoById(id));
    }
}
