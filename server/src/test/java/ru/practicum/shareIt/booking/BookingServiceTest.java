package ru.practicum.shareIt.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImplementation;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImplementation bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void getTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);



        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));

        BookingResponseDto responseDto = bookingService.get(booking.getId(), owner.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getTest_UnrelatedUser() {
        User owner = getUser(1);
        User booker = getUser(2);
        User unrelated = getUser(3);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.get(booking.getId(), unrelated.getId());
        });

        assertThat(e.getStatusCode(), equalTo(NOT_FOUND));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getAllByStateTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item1 = getItem(10, owner);
        Item item2 = getItem(11, owner);

        Booking booking1 = getBooking(100, booker, item1);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(9));
        Booking booking2 = getBooking(101, booker, item2);
        booking2.setStart(LocalDateTime.now().minusDays(8));
        booking2.setEnd(LocalDateTime.now().minusDays(7));

        List<Booking> bookingList = Arrays.asList(
                booking1,
                booking2
        );

        when(userService.findUserOrNot(eq(booker.getId()))).thenReturn(booker);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(booker.getId()))).thenReturn(bookingList);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.WAITING))).thenReturn(bookingList);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.REJECTED))).thenReturn(bookingList);

        List<BookingResponseDto> responseDtoList;

        responseDtoList = bookingService.getAllByState(RequestStatus.ALL, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestStatus.PAST, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestStatus.FUTURE, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestStatus.CURRENT, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestStatus.WAITING, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestStatus.REJECTED, booker.getId(), 0, 10);

        assertThat(responseDtoList.get(0).getId(), equalTo(booking1.getId()));
        assertThat(responseDtoList.get(1).getId(), equalTo(booking2.getId()));

        verify(userService, times(6)).findUserOrNot(eq(booker.getId()));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(eq(booker.getId()));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(booker.getId()),(eq(BookingStatus.WAITING)));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(booker.getId()),(eq(BookingStatus.REJECTED)));

//        verifyNoMoreInteractions(userService, bookingRepository);
    }

    @Test
    void createTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        when(userService.findUserOrNot(eq(booker.getId()))).thenReturn(booker);
        when(itemService.findItemOrNot(eq(item.getId()))).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto responseDto = bookingService.create(requestDto, booker.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(userService, times(1)).findUserOrNot(eq(booker.getId()));
        verify(itemService, times(1)).findItemOrNot(eq(item.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest_NotAvailableItem() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);
        item.setAvailable(false);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        when(userService.findUserOrNot(eq(booker.getId()))).thenReturn(booker);
        when(itemService.findItemOrNot(eq(item.getId()))).thenReturn(item);

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
                    bookingService.create(requestDto, booker.getId());
        });
        assertThat(e.getStatusCode(), equalTo(BAD_REQUEST));

        verify(userService, times(1)).findUserOrNot(eq(booker.getId()));
        verify(itemService, times(1)).findItemOrNot(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest_BookOwnItem() {
        User owner = getUser(1);

        Item item = getItem(10, owner);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        when(userService.findUserOrNot(eq(owner.getId()))).thenReturn(owner);
        when(itemService.findItemOrNot(eq(item.getId()))).thenReturn(item);

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.create(requestDto, owner.getId());
        });

        assertThat(e.getStatusCode(), equalTo(NOT_FOUND));

        verify(userService, times(1)).findUserOrNot(eq(owner.getId()));
        verify(itemService, times(1)).findItemOrNot(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void approveTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);

        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));

        BookingResponseDto responseDto = bookingService.approve(booking.getId(), true, owner.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void approveTest_ByNotOwner() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);
        booking.setStatus(BookingStatus.WAITING);

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.approve(booking.getId(), true, booker.getId());
        });

        assertThat(e.getStatusCode(), equalTo(NOT_FOUND));
    }
//
//    @Test
//    void approveTest_ForNotWaitingBooking() {
//        User owner = getUser(1);
//        User booker = getUser(2);
//
//        Item item = getItem(10, owner);
//
//        Booking booking = getBooking(100, booker, item);
//        booking.setStatus(BookingStatus.APPROVED);
//
//        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
//        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));
//
//        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
//            bookingService.approve(booking.getId(), true, owner.getId());
//        });
//
//        assertThat(e.getStatusCode(), equalTo(400));
//
//        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
//        verify(userRepository, times(1)).findById(eq(owner.getId()));
//        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
//    }

    private User getUser(long id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .email("user" + id + "@user.com")
                .build();
    }

    private Item getItem(long id, User owner) {
        return Item.builder()
                .id(id)
                .name("Item " + id)
                .description("ItemDescription " + id)
                .available(true)
                .owner(owner)
                .build();
    }

    private Booking getBooking(long id, User booker, Item item) {
        return Booking.builder()
                .id(id)
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();
    }
}
