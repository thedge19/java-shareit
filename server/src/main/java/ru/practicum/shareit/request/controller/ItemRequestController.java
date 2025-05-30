package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USERID_HEADER) int requesterId, @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Создаем запрос на вещь с описанием {}", itemRequestCreateDto.getDescription());
        return itemRequestService.create(itemRequestCreateDto, requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests() {
        log.info("Выводим все запросы");
        return itemRequestService.getAllRequests();
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(USERID_HEADER) long requesterId) {
        log.info("Выводим все запросы от пользователя с id={}", requesterId);
        return itemRequestService.getAllUserRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getItemRequestById(@PathVariable int requestId) {
        log.info("Ищем вещи по запросу с id={}", requestId);
        return itemRequestService.getByRequestId(requestId);
    }
}
