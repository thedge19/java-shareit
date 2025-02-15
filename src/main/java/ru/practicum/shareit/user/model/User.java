package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.Marker;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@Data
public class User {
    private Long id;
    @NotEmpty(groups = Marker.OnCreate.class)
    private String name;
    @NotEmpty(groups = Marker.OnCreate.class)
    private String email;
}
