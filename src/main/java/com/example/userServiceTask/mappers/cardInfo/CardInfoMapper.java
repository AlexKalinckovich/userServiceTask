package com.example.userServiceTask.mappers.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoCreateDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CardInfoUpdateDto;
import com.example.userServiceTask.model.cardInfo.CardInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true)
    })
    CardInfo fromCreateDto(CardInfoCreateDto cardInfoCreateDto);


    @Mapping(target = "userId", source = "user.id")
    CardInfoResponseDto toResponseDto(CardInfo cardInfo);

    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(CardInfoUpdateDto dto, @MappingTarget CardInfo entity);

    List<CardInfoResponseDto> toResponseDtoList(List<CardInfo> cardInfos);
}
