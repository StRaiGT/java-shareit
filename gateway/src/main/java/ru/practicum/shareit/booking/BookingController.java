package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.user.UserController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@RequestHeader(UserController.headerUserId) Long userId,
									  @PathVariable Long id) {
		return bookingClient.getById(userId, id);
	}

	@GetMapping
	public ResponseEntity<Object> getAllByBookerId(
			@RequestHeader(UserController.headerUserId) Long userId,
			@RequestParam(defaultValue = "ALL", required = false) String state,
			@RequestParam(defaultValue = UserController.PAGE_DEFAULT_FROM, required = false) @PositiveOrZero Integer from,
			@RequestParam(defaultValue = UserController.PAGE_DEFAULT_SIZE, required = false) @Positive Integer size) {
		State stateEnum = State.stringToState(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		return bookingClient.getAllByBookerId(userId, stateEnum, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwnerId(
			@RequestHeader(UserController.headerUserId) Long userId,
			@RequestParam(defaultValue = "ALL", required = false) String state,
			@RequestParam(defaultValue = UserController.PAGE_DEFAULT_FROM, required = false) @PositiveOrZero Integer from,
			@RequestParam(defaultValue = UserController.PAGE_DEFAULT_SIZE, required = false) @Positive Integer size) {
		State stateEnum = State.stringToState(state).orElseThrow(
				() -> new IllegalArgumentException("Unknown state: " + state));
		return bookingClient.getAllByOwnerId(userId, stateEnum, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(UserController.headerUserId) Long userId,
									 @Valid @RequestBody BookingRequestDto bookingRequestDto) {
		return bookingClient.create(userId, bookingRequestDto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> patch(@RequestHeader(UserController.headerUserId) Long userId,
									@PathVariable Long id,
									@RequestParam Boolean approved) {
		return bookingClient.patch(userId, id, approved);
	}
}