package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUser(long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    void deleteUser(long id);
}
