package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookingResponseUserDto booker;
    private BookingResponseItemDto item;

    @Data
    public static class BookingResponseItemDto {
        private Long id;
        private String name;
    }

    @Data
    public static class BookingResponseUserDto {
        private Long id;
    }
}
