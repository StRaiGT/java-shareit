package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "mapOwnerIdFromOwner")
    public abstract ItemDto toItemDto(Item item);

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapOwnerFromOwnerId")
    public abstract Item toItem(ItemDto itemDto);

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "mapOwnerIdFromOwner")
    @Mapping(target = "lastBooking", expression = "java(addLastBooking(item))")
    @Mapping(target = "nextBooking", expression = "java(addNextBooking(item))")
    public abstract ItemExtendedDto toItemExtendedDto(Item item);

    @Mapping(source = "booker", target = "bookerId", qualifiedByName = "mapBookerIdFromBooker")
    public abstract BookingItemDto bookingToBookingItemDto(Booking booking);

    @Named("mapOwnerIdFromOwner")
    @Transactional(readOnly = true)
    Long mapOwnerIdFromOwner(User user) {
        return user.getId();
    }

    @Named("mapOwnerFromOwnerId")
    @Transactional(readOnly = true)
    User mapOwnerFromOwnerId(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));
    }

    @Named("addLastBooking")
    @Transactional(readOnly = true)
    public BookingItemDto addLastBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                item.getId(), LocalDateTime.now(), Status.APPROVED);

        if (bookings.isEmpty()) {
            return null;
        }

        Booking lastBooking = bookings.get(0);
        return bookingToBookingItemDto(lastBooking);
    }

    @Named("addNextBooking")
    @Transactional(readOnly = true)
    public BookingItemDto addNextBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(
                item.getId(), LocalDateTime.now(), Status.APPROVED);

        if (bookings.isEmpty()) {
            return null;
        }

        Booking nextBooking = bookings.get(0);
        return bookingToBookingItemDto(nextBooking);
    }

    @Named("mapBookerIdFromBooker")
    @Transactional(readOnly = true)
    Long mapBookerIdFromBooker(User user) {
        return user.getId();
    }
}
