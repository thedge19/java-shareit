package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface UserStorage {

    User get(long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    Set<String> getEmails();

    void delete(long id);
}
