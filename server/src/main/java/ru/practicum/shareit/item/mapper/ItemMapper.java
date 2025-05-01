package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDto itemToItemDto(Item item);

    Item itemDtoToItem(ItemDto itemDto);

    default List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }
}
