package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InternalErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        List<UserDto> userDtos = userRepository.findAll().stream().map(UserMapper.INSTANCE::userToUserDto).toList();

        for (UserDto filteredUserDto : userDtos) {
            if (Objects.equals(userDto, filteredUserDto)) {
                throw new InternalErrorException("Данный пользователь уже зарегистрирован");
            }
        }

        User user = UserMapper.INSTANCE.userDtoToUser(userDto);

        if (emailCheck(user)) throw new InternalErrorException("Данная почта уже используется");

        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

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
        userRepository.findById(id);
        userRepository.deleteById(id);
    }

    @Override
    public User findUserOrNot(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private boolean emailCheck(User user) {

        return userRepository.findAll().stream().anyMatch(u -> user.getEmail().equals(u.getEmail()));
    }
}
