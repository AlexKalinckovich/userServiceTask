package com.example.userServiceTask.mappers.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    CardInfo fromCreateDto(CreateCardInfoDto createCardInfoDto);


    @Mappings({
            @Mapping(target = "userId",source = "user", qualifiedByName = "userToUserId")
    })
    CardInfoResponseDto toResponseDto(CardInfo cardInfo);

    @Named("userToUserId")
    default Long userToUserId(final User user) {
        return user == null ? null : user.getId();
    }
}
