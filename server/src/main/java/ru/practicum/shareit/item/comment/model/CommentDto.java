package ru.practicum.shareit.item.comment.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class CommentDto {
    Long id;
    String text;
    LocalDateTime createdDate;
    String authorName;
}
