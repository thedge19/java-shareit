package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;

    private ItemBooking nextBooking;
    private ItemBooking lastBooking;

    private List<CommentDto> comments;

    @Data
    @Builder
    public static class ItemBooking {
        private long id;
        private long bookerId;
    }
}
