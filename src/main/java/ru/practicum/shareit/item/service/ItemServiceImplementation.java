package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Primary
@Service
@RequiredArgsConstructor
public class ItemServiceImplementation implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImplementation.class);
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto get(long id) {

        if (itemStorage.get(id) == null) {
            throw new NotFoundException("Item not found");
        }

        return ItemMapper.toItemDto(itemStorage.get(id));
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        return itemStorage.getAll(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userStorage.get(userId);

        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }

        if (owner == null) {
            throw new NotFoundException("User not found");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available not found");
        }

        return itemStorage.addItem(itemDto, owner);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemStorage.get(itemId);

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("You are not allowed to update this item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemStorage.updateItem(item, itemId);
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText.isBlank()) {
            return Collections.EMPTY_LIST;
        }

        searchText = searchText.toLowerCase();

        return itemStorage.search(searchText).stream().map(ItemMapper::toItemDto).toList();
    }
}
