package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.model.CommentDto;
import ru.practicum.shareit.item.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.controller.UserController;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemExtendedDto> getByOwnerId(@RequestHeader(UserController.headerUserId) Long userId,
                                              @RequestParam Integer from,
                                              @RequestParam Integer size) {
        return itemService.getByOwnerId(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public ItemExtendedDto getById(@RequestHeader(UserController.headerUserId) Long userId,
                                   @PathVariable Long id) {
        return itemService.getById(userId, id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(UserController.headerUserId) Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader(UserController.headerUserId) Long userId,
                         @PathVariable Long id,
                         @RequestBody ItemDto itemDto) {
        return itemService.patch(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam Integer from,
                                @RequestParam Integer size) {
        return itemService.search(text, PageRequest.of(from / size, size));
    }

    @PostMapping("{id}/comment")
    public CommentDto addComment(@RequestHeader(UserController.headerUserId) Long userId,
                                 @PathVariable Long id,
                                 @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, id, commentRequestDto);
    }
}
