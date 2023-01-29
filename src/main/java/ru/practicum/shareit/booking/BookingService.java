package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto getById(Long userId, Long id);

    List<BookingResponseDto> getAllByBookerId(Long userId, String state);

    List<BookingResponseDto> getAllByOwnerId(Long userId, String state);

    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto patch(Long userId, Long id, Boolean approved);
}
