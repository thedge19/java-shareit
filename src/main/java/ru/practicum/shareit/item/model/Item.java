package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@NoArgsConstructor
public class Item {
    private Long id;
    @NotEmpty(groups = Marker.OnCreate.class)
    private String name;
    @NotEmpty(groups = Marker.OnCreate.class)
    private String description;
    @NotEmpty(groups = Marker.OnCreate.class)
    private Boolean available;

    private User owner;

    private ItemRequest itemRequest;
}
