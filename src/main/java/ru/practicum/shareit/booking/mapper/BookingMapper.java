package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "item", expression = "java(item)")
    @Mapping(target = "booker", expression = "java(user)")
    Booking requestDtoToBooking(BookingRequestDto bookingRequestDto, Item item, User user, Status status);

    BookingResponseDto bookingToBookingResponseDto(Booking booking);
}

