package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingRequestDto {
    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
