package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.model.CommentDto;
import ru.practicum.shareit.item.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemExtendedDto;

import java.util.List;

public interface ItemService {
    List<ItemExtendedDto> getByOwnerId(Long userId);

    ItemExtendedDto getById(Long userId, Long id);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto patch(Long userId, Long id, ItemDto itemDto);

    void delete(Long id);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long id, CommentRequestDto commentRequestDto);

    Item getItemById(Long id);
}
