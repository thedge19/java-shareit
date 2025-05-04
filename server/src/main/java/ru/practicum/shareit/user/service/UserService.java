package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUser(long id);

    UserDto updateUser(UserDto userDto, long id);

    void deleteUser(long id);

    User findUserOrNot(long id);

    List<UserDto> getAll();
}
