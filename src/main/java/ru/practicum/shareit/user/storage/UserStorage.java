package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User get(long id);

    Collection<User> getAllUsers();

    User create(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    void delete(long id);
}
