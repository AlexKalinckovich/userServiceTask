package com.example.userServiceTask.mappers.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "userId", target = "user", qualifiedByName = "userIdToUser")
    })
    CardInfo fromCreateDto(CreateCardInfoDto createCardInfoDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateCardInfoDto updateCardInfoDto, @MappingTarget CardInfo cardInfo);

    CardInfoResponseDto toResponseDto(CardInfo cardInfo);

    @Named("userIdToUser")
    default User userIdToUser(Long userId) {
        return userId == null ?
                null : User.builder().id(userId).build();
    }

}
