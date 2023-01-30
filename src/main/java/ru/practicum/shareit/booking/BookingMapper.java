package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public abstract class BookingMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Mapping(source = "bookerId", target = "booker", qualifiedByName = "mapUserDtoFromId")
    @Mapping(source = "itemId", target = "item", qualifiedByName = "mapItemDtoFromId")
    public abstract Booking requestDtoToBooking(BookingRequestDto bookingRequestDto);

    public abstract Booking responseDtoToBooking(BookingResponseDto bookingResponseDto);

    public abstract BookingResponseDto bookingToBookingResponseDto(Booking booking);

    @Named("mapUserDtoFromId")
    @Transactional(readOnly = true)
    User mapUserDtoFromId(Long bookerId) {
        return userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));
    }

    @Named("mapItemDtoFromId")
    @Transactional(readOnly = true)
    Item mapItemDtoFromId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует."));
    }
}

