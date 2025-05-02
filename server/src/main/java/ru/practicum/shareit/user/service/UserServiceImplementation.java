package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.INSTANCE.userDtoToUser(userDto);
        try {
            return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.INSTANCE.userToUserDto(findUserOrNot(id));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User updatedUser = findUserOrNot(id);
        Optional.ofNullable(userDto.getName()).ifPresent(updatedUser::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(updatedUser::setEmail);

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
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::userToUserDto)
                .toList();
    }
}
