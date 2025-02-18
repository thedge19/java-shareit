package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exception.Marker;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Data
public class ItemDto {
    private Long id;
    @NotEmpty(groups = Marker.OnCreate.class)
    private String name;
    @NotEmpty(groups = Marker.OnCreate.class)
    private String description;

    @NotEmpty(groups = Marker.OnCreate.class)
    private Boolean available;

    private Integer requestId;
}
