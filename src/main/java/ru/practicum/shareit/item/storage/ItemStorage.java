package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {

    Item get(long id);

    List<Item> getAll(long userId);

    ItemDto addItem(ItemDto itemDto, User owner);

    ItemDto updateItem(Item item, Long itemId);

    List<Item> search(String searchText);

}
