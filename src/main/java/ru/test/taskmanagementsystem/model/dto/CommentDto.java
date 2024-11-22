package ru.test.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
    @NotNull(message = "Автор комментария не может быть пустым")
    private String username;
}
