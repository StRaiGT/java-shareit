package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemExtendedDto> getByOwnerId(Long userId) {
        log.info("Вывод всех вещей пользователя с id {}.", userId);
        return itemRepository.getAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::toItemExtendedDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemExtendedDto getById(Long userId, Long id) {
        log.info("Вывод вещи с id {}.", id);
        ItemExtendedDto itemExtendedDto = itemMapper.toItemExtendedDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует.")));

        if (!Objects.equals(userId, itemExtendedDto.getOwnerId())) {
            itemExtendedDto.setLastBooking(null);
            itemExtendedDto.setNextBooking(null);
        }

        return itemExtendedDto;
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Создание вещи {} пользователем с id {}.", itemDto, userId);

        userService.getById(userId);

        itemDto.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto)));
    }

    @Override
    @Transactional
    public ItemDto patch(Long userId, Long id, ItemDto itemDto) {
        log.info("Обновление вещи {} с id {} пользователем с id {}.", itemDto, id, userId);

        Item repoItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует."));
        if (!Objects.equals(userId, repoItem.getOwner().getId())) {
            throw new ForbiddenException("Изменение вещи доступно только владельцу.");
        }

        itemDto.setOwnerId(userId);
        itemDto.setId(id);
        Item item = itemMapper.toItem(itemDto);

        if (item.getName() != null) {
            repoItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            repoItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            repoItem.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(repoItem));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление вещи с id {}.", id);
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Поиск вещей с подстрокой \"{}\".", text);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
