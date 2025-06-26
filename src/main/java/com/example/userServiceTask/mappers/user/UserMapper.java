package com.example.userServiceTask.mappers.user;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;

import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = CardInfoMapper.class)
public interface UserMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "cards", ignore = true, qualifiedByName = "setDefaultEmptyList")}
    )
    User createFromDto(CreateUserDto userDto);

    @Mapping(source = "cards", target = "cards")
    UserResponseDto toResponseDto(User entity);

    @Named("setDefaultEmptyList")
    default List<CardInfo> setDefaultEmptyList(){
        return List.of();
    }

}
