package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Integer id;
    @NotBlank(message = "Текст комментария не должен быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}