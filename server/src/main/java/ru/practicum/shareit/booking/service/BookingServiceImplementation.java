package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImplementation implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto requestDto, long bookerId) {
        User booker = userService.findUserOrNot(bookerId);
        Item bookingItem = itemService.findItemOrNot(requestDto.getItemId());

        if (!bookingItem.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (bookingItem.getOwner().getId() == bookerId) {
            throw new NotFoundException("Владелец не может бронировать свою вестчь");
        }

        Booking booking = BookingMapper.INSTANCE.bookingRequestDtoToBooking(requestDto);
        booking.setBooker(booker);
        booking.setItem(bookingItem);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.INSTANCE.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto approve(long bookingId, boolean approved, long userId) {
        Booking updatedBooking = getBookingOrNot(bookingId);

        if (updatedBooking.getItem().getOwner().getId() != userId) {
            throw new BadRequestException("Подтверждение доступно только для владельца вещи");
        }

        if (updatedBooking.getStatus() != BookingStatus.WAITING) {
            throw new NotFoundException("Вещь не ожидает подтверждения");
        }

        updatedBooking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.INSTANCE.bookingToBookingResponseDto(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto get(long bookingId, long bookerId) {
        Booking booking = getBookingOrNot(bookingId);
        userService.findUserOrNot(bookerId);

        if (!(booking.getBooker().getId() == bookerId || booking.getItem().getOwner().getId() == bookerId)) {
            throw new NotFoundException("Не найдено подходящих бронирований для пользователя " + bookerId);
        }

        return BookingMapper.INSTANCE.bookingToBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAll(long bookerId, RequestStatus state) {
        userService.findUserOrNot(bookerId);

        return bookingRepository
                .findAllByBookerIdOrderByStartDesc(bookerId)
                .stream()
                .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getAllOwnersItems(RequestStatus requestStatus, long ownerId) {
        userService.findUserOrNot(ownerId);

        return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId)
                .stream()
                .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                .toList();
    }

    @Override
    public Booking getBookingOrNot(long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
}
