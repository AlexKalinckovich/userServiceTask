package com.example.userServiceTask.mappers.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateCardInfoDto dto, @MappingTarget CardInfo entity);

    @Named("userToUserId")
    default Long userToUserId(final User user) {
        return user == null ? null : user.getId();
    }
}
