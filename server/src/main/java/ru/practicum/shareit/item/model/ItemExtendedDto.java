package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.item.comment.model.CommentDto;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class ItemExtendedDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments;
}
