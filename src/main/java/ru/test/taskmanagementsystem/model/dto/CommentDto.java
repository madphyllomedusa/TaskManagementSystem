package ru.test.taskmanagementsystem.model.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private String username;
}
