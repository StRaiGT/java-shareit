package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Создание запроса вещи {} пользователем с id {}.", itemRequestCreateDto, userId);

        User user = userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreateDto, user, LocalDateTime.now());

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestExtendedDto getById(Long userId, Long id) {
        log.info("Вывод запроса вещи с id {} пользователем с id {}.", id, userId);

        userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запроса вещи с таким id не существует."));

        List<ItemDto> items = itemRequest.getItems().stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        return itemRequestMapper.toItemRequestExtendedDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestExtendedDto> getByRequesterId(Long userId) {
        log.info("Вывод всех запросов вещей пользователем с id {}.", userId);

        userService.getUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequesterId_IdOrderByCreatedAsc(userId);

        return itemRequests.stream()
                .map((itemRequest) -> itemRequestMapper.toItemRequestExtendedDto(
                        itemRequest,
                        itemRequest.getItems()
                                .stream()
                                .map(itemMapper::toItemDto)
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestExtendedDto> getAll(Long userId, int from, int size) {
        log.info("Вывод всех запросов вещей постранично from {} size {}.", from, size);

        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        Page<ItemRequest> itemRequests = itemRequestRepository.getAllByRequesterId_IdNot(userId, pageable);

        return itemRequests.stream()
                .map((itemRequest) -> itemRequestMapper.toItemRequestExtendedDto(
                        itemRequest,
                        itemRequest.getItems()
                                .stream()
                                .map(itemMapper::toItemDto)
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }
}
