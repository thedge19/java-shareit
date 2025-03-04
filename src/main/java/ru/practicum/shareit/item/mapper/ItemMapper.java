package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

//    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDto itemToItemDto(Item item);

    Item itemDtoToItem(ItemDto itemDto);
}
