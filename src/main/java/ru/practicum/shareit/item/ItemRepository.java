package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    List<Item> getItemsByOwner(Long userId);
    Item getItemById(Long id);
    Item createItem(Item item);
    Item patchItem(Item item);
    Boolean deleteItem(Long id);
    List<Item> search(String text);
}
