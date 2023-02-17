package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getById(Long userId, Long id);

    List<BookingResponseDto> getAllByBookerId(Long userId, State state, Pageable pageable);

    List<BookingResponseDto> getAllByOwnerId(Long userId, State state, Pageable pageable);

    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto patch(Long userId, Long id, Boolean approved);
}
