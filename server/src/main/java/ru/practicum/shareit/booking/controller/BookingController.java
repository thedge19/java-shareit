package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String BOOKER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader(BOOKER_ID) long bookerId,
            @Valid @RequestBody BookingRequestDto requestDto) {
        log.info("Создаётся бронирование: {}", requestDto);
        BookingResponseDto responseDto = bookingService.create(requestDto, bookerId);
        log.info("Бронирование {} создано", responseDto);

        return responseDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(BOOKER_ID) long bookerId) {
        log.info("Обновляется бронирование с id={}", bookingId);
        BookingResponseDto responseDto = bookingService.approve(bookingId, approved, bookerId);
        log.info("Обновлено бронирование {}", responseDto);
        return responseDto;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto get(
            @PathVariable long bookingId,
            @RequestHeader(BOOKER_ID) long bookerId) {
        log.info("Запрашивается информация по бронированию с id={}", bookingId);
        return bookingService.get(bookingId, bookerId);
    }

    @GetMapping()
    public List<BookingResponseDto> getAll(
            @RequestParam(defaultValue = "ALL") RequestStatus state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestHeader(BOOKER_ID) long bookerId
            ) {
        log.info("Запрашиваются бронирования пользователя с id={} и state={}", bookerId, state);
        return bookingService.getAllByState(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") RequestStatus state,
                                                       @RequestHeader(BOOKER_ID) int ownerId) {
        log.info("Запрашиваются бронирования всех вещей пользователя с id={}", ownerId);
        return bookingService.getAllOwnersItems(state, ownerId);
    }
}
