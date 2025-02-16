package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto getUser(long id) {
        return UserMapper.toUserDto(userStorage.get(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        List<UserDto> userDtos = userStorage.getAllUsers().stream().map(UserMapper::toUserDto).toList();
        
        for (UserDto filteredUserDto : userDtos) {
            if (Objects.equals(userDto, filteredUserDto)) {
                throw new InternalErrorException("Данный пользователь уже зарегистрирован");
            }
        }

        return UserMapper.toUserDto(userStorage.create(userDto));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        return UserMapper.toUserDto(userStorage.update(userDto, id));
    }

    @Override
    public void deleteUser(long id) {
        if (userStorage.get(id) == null) {
            throw new NotFoundException("Пользователь с заданным id не найден");
        }
        userStorage.delete(id);
    }
}
