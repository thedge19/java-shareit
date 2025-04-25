package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest getItemRequestOrNot(int id);

    ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, long requesterId);

    List<ItemRequestDto> getAllUserRequests(long requesterId);

    List<ItemRequestDto> getAllRequests();

    ItemRequestWithItemsDto getByRequestId(Integer requesterId);
}
