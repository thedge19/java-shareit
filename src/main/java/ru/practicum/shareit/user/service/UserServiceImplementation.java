package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class UserServiceImplementation implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto getUser(long id) {
        return UserMapper.toUserDto(userStorage.get(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (userStorage.getEmails().contains(email) || !isValidEmail(email)) {
            throw new InternalErrorException("Некорректный email");
        }

        return userStorage.create(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User updatedUser = userStorage.get(id);
        String email = userDto.getEmail();
        if (userStorage.getEmails().contains(email) && !Objects.equals(updatedUser.getEmail(), email)) {
            throw new InternalErrorException("Некорректный email");
        }

        return userStorage.update(userDto, id);
    }

    @Override
    public void deleteUser(long id) {
        userStorage.delete(id);
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$");
    }
}
