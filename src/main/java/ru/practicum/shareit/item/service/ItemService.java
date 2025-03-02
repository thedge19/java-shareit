package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto get(long id, long userId);

    List<ItemDto> getAll(long userId);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    List<ItemDto> search(String searchText);

    Item findItemOrNot(long id);

    CommentDto createComment(CommentDto commentDto, long userId, long itemId);


}
