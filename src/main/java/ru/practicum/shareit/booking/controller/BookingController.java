package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Constrains;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingResponseDto getById(@RequestHeader(Constrains.headerUserId) Long userId,
                                      @PathVariable Long id) {
        return bookingService.getById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBookerId(@RequestHeader(Constrains.headerUserId) Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state) {
        State stateEnum = State.stringToState(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));

        return bookingService.getAllByBookerId(userId, stateEnum);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerId(@RequestHeader(Constrains.headerUserId) Long userId,
                                                    @RequestParam(defaultValue = "ALL", required = false) String state) {
        State stateEnum = State.stringToState(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));

        return bookingService.getAllByOwnerId(userId, stateEnum);
    }

    @PostMapping
    public BookingResponseDto create(@RequestHeader(Constrains.headerUserId) Long userId,
                                    @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto patch(@RequestHeader(Constrains.headerUserId) Long userId,
                                   @PathVariable Long id,
                                   @RequestParam() Boolean approved) {
        return bookingService.patch(userId, id, approved);
    }
}