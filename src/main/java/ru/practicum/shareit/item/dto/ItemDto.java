package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exception.Marker;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Data
public class ItemDto {
    private Long id;

    @NotBlank(groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;

    private Long requestId;

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
