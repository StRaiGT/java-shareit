package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.user.controller.UserController;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingResponseDto getById(@RequestHeader(UserController.headerUserId) Long userId,
                                      @PathVariable Long id) {
        return bookingService.getById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBookerId(@RequestHeader(UserController.headerUserId) Long userId,
                                                     @RequestParam String state,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size) {
        return bookingService.getAllByBookerId(userId, State.valueOf(state), PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerId(@RequestHeader(UserController.headerUserId) Long userId,
                                                    @RequestParam String state,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        return bookingService.getAllByOwnerId(userId, State.valueOf(state), PageRequest.of(from / size, size));
    }

    @PostMapping
    public BookingResponseDto create(@RequestHeader(UserController.headerUserId) Long userId,
                                     @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto patch(@RequestHeader(UserController.headerUserId) Long userId,
                                   @PathVariable Long id,
                                   @RequestParam Boolean approved) {
        return bookingService.patch(userId, id, approved);
    }
}
