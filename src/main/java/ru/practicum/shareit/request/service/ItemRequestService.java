package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    ItemRequestExtendedDto getById(Long userId, Long id);

    List<ItemRequestExtendedDto> getByRequesterId(Long userId);

    List<ItemRequestExtendedDto> getAll(Long userId, int from, int size);
}
