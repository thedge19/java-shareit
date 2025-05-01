package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImplementation implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto requestDto, long bookerId) {
        User booker = userService.findUserOrNot(bookerId);
        Item bookingItem = itemService.findItemOrNot(requestDto.getItemId());

        if (!bookingItem.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь недоступна для бронирования");
        }

        if (bookingItem.getOwner().getId() == bookerId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может бронировать свою вестчь");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Подтверждение доступно только для владельца вещи");
        }

        if (updatedBooking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь не ожидает подтверждения");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдено подходящих бронирований для пользователя " + bookerId);
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
        return bookingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllByState(RequestStatus requestBookingStatus, long userId, int from, int size) {

        userService.findUserOrNot(userId);

        return switch (requestBookingStatus) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                    .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                    .collect(Collectors.toList());
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                            .collect(Collectors.toList());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                            .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                            .collect(Collectors.toList());
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                            .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                            .collect(Collectors.toList());
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING).stream()
                            .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                            .collect(Collectors.toList());
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED).stream()
                            .map(BookingMapper.INSTANCE::bookingToBookingResponseDto)
                            .collect(Collectors.toList());
            default -> throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Неизвестный статус бронирования");
        };
    }
}
