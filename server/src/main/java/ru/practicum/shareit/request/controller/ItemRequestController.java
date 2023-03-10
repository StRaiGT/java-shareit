package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.controller.UserController;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(UserController.headerUserId) Long userId,
                                 @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping("/{id}")
    public ItemRequestExtendedDto getById(@RequestHeader(UserController.headerUserId) Long userId,
                                          @PathVariable Long id) {
        return itemRequestService.getById(userId, id);
    }

    @GetMapping
    public List<ItemRequestExtendedDto> getByRequesterId(@RequestHeader(UserController.headerUserId) Long userId) {
        return itemRequestService.getByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestExtendedDto> getAll(@RequestHeader(UserController.headerUserId) Long userId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return itemRequestService.getAll(userId, PageRequest.of(from / size, size));
    }
}
