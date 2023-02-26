package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.user.UserController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(UserController.headerUserId) Long userId,
                                         @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestClient.create(userId, itemRequestCreateDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(UserController.headerUserId) Long userId,
                                          @PathVariable Long id) {
        return itemRequestClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequesterId(@RequestHeader(UserController.headerUserId) Long userId) {
        return itemRequestClient.getByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader(UserController.headerUserId) Long userId,
            @RequestParam(defaultValue = UserController.PAGE_DEFAULT_FROM, required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = UserController.PAGE_DEFAULT_SIZE, required = false) @Positive Integer size) {
        return itemRequestClient.getAll(userId, from, size);
    }
}
