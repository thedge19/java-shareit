package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto requestDto, long userId);

    BookingResponseDto approve(long bookingId, boolean approved, long userId);

    Booking getBookingOrNot(long id);

    BookingResponseDto get(long bookingId, long bookerId);

    List<BookingResponseDto> getAll(long bookerId, RequestStatus state);

    List<BookingResponseDto> getAllOwnersItems(RequestStatus requestBookingStatus, long ownerId);

    List<BookingResponseDto> getAllByStateForOwner(RequestStatus requestBookingStatus, long userId, int from, int size);
}
