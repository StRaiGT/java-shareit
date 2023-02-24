package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@ToString
public class BookingRequestDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
}
