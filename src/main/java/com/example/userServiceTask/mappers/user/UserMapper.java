package com.example.userServiceTask.mappers.user;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;

import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.CardInfo;
import com.example.userServiceTask.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = CardInfoMapper.class)
public interface UserMapper {

    default User createFromDto(CreateUserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .email(userDto.getEmail())
                .birthDate(userDto.getBirthDate())
                .cards(new ArrayList<>())
                .build();
    }

    @Mapping(source = "cards", target = "cards")
    UserResponseDto toResponseDto(User entity);

}
