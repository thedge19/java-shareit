package ru.practicum.shareit.item.controller;

import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ItemDto get(
            @PathVariable long id
    ) {
        log.info("Запрашивается вещь с Id={}", id);
        return itemService.get(id);
    }

    @GetMapping
    public List<ItemDto> getAll(
            @RequestHeader(USER_ID) long userId
    ) {
        return itemService.getAll(userId);
    }

    @PostMapping
    public ItemDto createItem(
            @Validated(Marker.OnCreate.class)
            @RequestHeader(USER_ID) long userId,
            @RequestBody ItemDto itemDto) {

        log.info("Создаётся новая вещь: {} пользователем id={}", itemDto, userId);

        ItemDto createdItem = itemService.createItem(itemDto, userId);
        log.info("Вещь {} создана", createdItem);

        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable long itemId,
            @RequestHeader(USER_ID) long userId,
            @RequestBody ItemDto itemDto) {
        log.info("Обновляется вещь id={}, {}, пользователем id={}", itemId, itemDto, userId);
        ItemDto updatedItem = itemService.updateItem(itemDto, itemId, userId);
        log.info("Обновлена вещь {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam(name = "text") @Size(max = 15) String searchText) {
        log.info("Поиск вещей с текстом {}", searchText);
        List<ItemDto> items = itemService.search(searchText);
        log.info("Список найденных вещей длиной: {}", items.size());
        return itemService.search(searchText);
    }
}
