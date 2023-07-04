package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.api.UserShort;
import ru.practicum.ewm.dto.entities.UserDto;

public class UserMapper {

    public static UserShort toUserShort(UserDto userDto) {
        UserShort userShort = new UserShort();
        if (userDto.getId() != null) {
            userShort.setId(userDto.getId());
        }
        if (userDto.getName() != null) {
            userShort.setName(userDto.getName());
        }
        return userShort;
    }

    public static UserDto toUserDto(UserShort userShort) {
        UserDto userDto = new UserDto();
        if (userShort.getId() != null) {
            userDto.setId(userShort.getId());
        }
        if (userShort.getName() != null) {
            userDto.setName(userShort.getName());
        }
        return userDto;
    }
}
