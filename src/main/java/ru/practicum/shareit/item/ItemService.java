package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long userId);

    ItemDto getItemById(Long id);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto patchItem(Long userId, Long id, ItemDto itemDto);

    Boolean deleteItem(Long id);

    List<ItemDto> search(String text);
}
