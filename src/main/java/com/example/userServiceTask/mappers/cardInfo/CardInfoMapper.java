package com.example.userServiceTask.mappers.cardInfo;

import com.example.userServiceTask.dto.cardInfo.CardInfoResponseDto;
import com.example.userServiceTask.dto.cardInfo.CreateCardInfoDto;
import com.example.userServiceTask.dto.cardInfo.UpdateCardInfoDto;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    CardInfo fromCreateDto(CreateCardInfoDto createCardInfoDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateCardInfoDto updateCardInfoDto, @MappingTarget CardInfo cardInfo);

    @Mappings({
            @Mapping(target = "userId",source = "user", qualifiedByName = "userToUserId")
    })
    CardInfoResponseDto toResponseDto(CardInfo cardInfo);

    @Named("userToUserId")
    default Long userToUserId(final User user) {
        return user == null ? null : user.getId();
    }
}
