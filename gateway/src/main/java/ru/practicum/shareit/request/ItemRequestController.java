package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final static String USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(USER_ID) long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return itemRequestClient.getAllByOwnerId(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID) long userId,
                                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID) long userId,
                                          @PathVariable int itemRequestId) {
        return itemRequestClient.getById(userId, itemRequestId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) long userId,
                                         @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {

        log.info("Контроллер");

        return itemRequestClient.create(userId, itemRequestCreateDto);
    }
}