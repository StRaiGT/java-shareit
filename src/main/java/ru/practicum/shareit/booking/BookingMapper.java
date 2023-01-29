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
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public abstract class BookingMapper {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRepository itemRepository;

    @Mapping(source = "bookerId", target = "booker", qualifiedByName = "mapUserDtoFromId")
    @Mapping(source = "itemId", target = "item", qualifiedByName = "mapItemDtoFromId")
    public abstract Booking requestDtoToBooking(BookingRequestDto bookingRequestDto);

    public abstract Booking responseDtoToBooking(BookingResponseDto bookingResponseDto);

    public abstract BookingResponseDto bookingToBookingResponseDto(Booking booking);

    @Named("mapUserDtoFromId")
    UserDto mapUserDtoFromId(Long bookerId) {
        return userService.getById(bookerId);
    }

    @Named("mapItemDtoFromId")
    @Transactional(readOnly = true)
    Item mapItemDtoFromId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует."));

    }

    public static BookingItemDto bookingToBookingItemDto(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}

