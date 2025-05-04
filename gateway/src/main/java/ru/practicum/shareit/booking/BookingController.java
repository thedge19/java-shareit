package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;


@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID) int userId,
                                          @PathVariable int bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByState(@RequestHeader(USER_ID) int userId,
                                                @RequestParam(defaultValue = "ALL") @Valid RequestBookingStatus state,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingClient.getAllByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByStateForOwner(@RequestHeader(USER_ID) int userId,
                                                        @RequestParam(defaultValue = "ALL") @Valid RequestBookingStatus state,
                                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                                        @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingClient.getAllByStateForOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) int userId,
                                         @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingClient.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID) int userId,
                                          @PathVariable int bookingId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }
}