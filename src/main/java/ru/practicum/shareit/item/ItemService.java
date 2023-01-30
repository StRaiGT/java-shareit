package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemExtendedDto> getByOwnerId(Long userId);

    ItemExtendedDto getById(Long userId, Long id);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto patch(Long userId, Long id, ItemDto itemDto);

    void delete(Long id);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long id, CommentRequestDto commentRequestDto);
}
