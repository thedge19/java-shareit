package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImplementation implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequest getItemRequestOrNot(int id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("ItemRequest not found"));
    }

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, long requestorId) {
        User user = userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Creating new item request");
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(itemRequestCreateDto, user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(long requestorId) {
        return ItemRequestMapper.INSTANCE.toItemRequestDtoList(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId));
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        return ItemRequestMapper.INSTANCE.toItemRequestDtoList(itemRequestRepository.findAll());
    }

    @Override
    public ItemRequestWithItemsDto getByRequestId(Integer requestId) {
        log.info("getByRequestId: {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> itemsForRequest = itemRepository.findByRequestIdIn(List.of(requestId));



        List<ItemDto> items = ItemMapper.INSTANCE.toItemDtoList(itemsForRequest);
        return  ItemRequestMapper.INSTANCE.toItemRequestWithItems(itemRequest, items);
    }

}
