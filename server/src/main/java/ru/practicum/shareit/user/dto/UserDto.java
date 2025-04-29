package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.Marker;

import java.util.Objects;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
