package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Primary
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public User get(long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = new User();

        long id = getNextId(users);

        user.setId(id);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        emails.add(userDto.getEmail());
        users.put(id, user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = users.get(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        users.put(id, user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public Set<String> getEmails() {
        return emails;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    private long getNextId(Map<Long, ?> elements) {
        long currentMaxId = elements.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
