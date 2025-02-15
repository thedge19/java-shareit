package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item get(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).toList();
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        long id = getNextId(items);
        item.setId(id);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable());

        items.put(id, item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Item item, Long itemId) {
        items.put(itemId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<Item> search(String searchText) {
        return items.values().stream().filter(item -> item.getName().toLowerCase().contains(searchText) && item.getAvailable() ||
                item.getDescription().toLowerCase().contains(searchText) && item.getAvailable()).toList();
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
