package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InternalErrorException;
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
    public User create(UserDto userDto) {
        User user = new User();

        long id = getNextId(users);

        user.setId(id);
        user.setName(userDto.getName());
        addEmail(user, userDto.getEmail());
        emails.add(userDto.getEmail());
        users.put(id, user);

        return user;
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = users.get(id);
        String oldEmail = userDto.getEmail();

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            addEmail(user, userDto.getEmail());
        }

        emails.remove(oldEmail);
        emails.add(userDto.getEmail());
        users.put(id, user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        emails.remove(users.get(id).getEmail());
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

    private void addEmail(User user, String email) {
        if (emails.contains(email) && !Objects.equals(user.getEmail(), email)) {
            throw new InternalErrorException("Некорректный email");
        }
        user.setEmail(email);
    }
}
