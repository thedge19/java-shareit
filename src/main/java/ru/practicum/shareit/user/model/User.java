package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.Marker;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
