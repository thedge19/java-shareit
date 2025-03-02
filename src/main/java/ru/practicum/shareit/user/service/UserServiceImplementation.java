package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {

    private final UserStorage userStorage;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        List<UserDto> userDtos = userStorage.findAll().stream().map(UserMapper.INSTANCE::userToUserDto).toList();
        for (UserDto filteredUserDto : userDtos) {
            if (Objects.equals(userDto, filteredUserDto)) {
                throw new InternalErrorException("Данный пользователь уже зарегистрирован");
            }
        }

        return UserMapper.INSTANCE.userToUserDto(userStorage.save(UserMapper.INSTANCE.userDtoToUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto getUser(long id) {
        return UserMapper.INSTANCE.userToUserDto(findUserOrNot(id));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User updatedUser = findUserOrNot(id);
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }

        return UserMapper.INSTANCE.userToUserDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        userStorage.findById(id);
        userStorage.deleteById(id);
    }

    @Override
    public User findUserOrNot(long id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
