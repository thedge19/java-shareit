package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class UserDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Email(message = "Некорректный e-mail")
    private String email;
}