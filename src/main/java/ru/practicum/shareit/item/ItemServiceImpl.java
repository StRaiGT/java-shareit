package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        log.info("Вывод всех вещей пользователя с id {}.", userId);
        return itemRepository.getItemsByOwner(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Вывод вещи с id {}.", id);
        return itemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Создание вещи {} пользователем с id {}.", itemDto, userId);
        itemDto.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.createItem(itemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto patchItem(Long userId, Long id, ItemDto itemDto) {
        log.info("Обновление вещи {} с id {} пользователем с id {}.", itemDto, id, userId);
        itemDto.setOwnerId(userId);
        itemDto.setId(id);
        return itemMapper.toItemDto(itemRepository.patchItem(itemMapper.toItem(itemDto)));
    }

    @Override
    public Boolean deleteItem(Long id) {
        log.info("Удаление вещи с id {}.", id);
        return itemRepository.deleteItem(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Поиск вещей с подстрокой \"{}\".", text);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        return itemRepository.search(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
