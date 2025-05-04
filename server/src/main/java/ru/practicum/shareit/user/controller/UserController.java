package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        log.info("Запрашивается пользователь с Id={}", id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public UserDto create(
            @Validated(Marker.OnCreate.class)
            @RequestBody UserDto userDto) {

        log.info("Creating user: {}", userDto);

        UserDto user = userService.createUser(userDto);
        log.info("Created user: {}", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId,
                          @RequestBody UserDto userDto
    ) {
        log.info("Updating user: id={} {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user: id={}", userId);
        userService.deleteUser(userId);
    }
}
