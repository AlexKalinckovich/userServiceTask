package com.example.userServiceTask.mappers.user;

import com.example.userServiceTask.dto.user.CreateUserDto;
import com.example.userServiceTask.dto.user.UserResponseDto;

import com.example.userServiceTask.dto.user.UserUpdateDto;
import com.example.userServiceTask.mappers.cardInfo.CardInfoMapper;
import com.example.userServiceTask.model.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserUpdateDto dto, @MappingTarget User entity);
}
