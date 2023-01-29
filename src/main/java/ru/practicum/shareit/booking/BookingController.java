package ru.practicum.shareit.booking;

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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public final BookingService bookingService;
    private final String headerUserId = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public BookingResponseDto getById(@RequestHeader(headerUserId) Long userId,
                                     @PathVariable Long id) {
        return bookingService.getById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBookerId(@RequestHeader(headerUserId) Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerId(@RequestHeader(headerUserId) Long userId,
                                                    @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllByOwnerId(userId, state);
    }

    @PostMapping
    public BookingResponseDto create(@RequestHeader(headerUserId) Long userId,
                                    @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto patch(@RequestHeader(headerUserId) Long userId,
                                   @PathVariable Long id,
                                   @RequestParam() Boolean approved) {
        return bookingService.patch(userId, id, approved);
    }
}
