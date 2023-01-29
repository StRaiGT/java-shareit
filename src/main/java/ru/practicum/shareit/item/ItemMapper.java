package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Autowired
    private BookingRepository bookingRepository;

    public abstract ItemDto toItemDto(Item item);

    public abstract Item toItem(ItemDto itemDto);

    @Mapping(target = "lastBooking", expression = "java(addLastBooking(item))")
    @Mapping(target = "nextBooking", expression = "java(addNextBooking(item))")
    public abstract ItemExtendedDto toItemExtendedDto(Item item);

    @Named("addLastBooking")
    @Transactional(readOnly = true)
    public BookingItemDto addLastBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                item.getId(), LocalDateTime.now(), Status.APPROVED);

        if (bookings.isEmpty()) {
            return null;
        }

        Booking lastBooking = bookings.get(0);
        return BookingMapper.bookingToBookingItemDto(lastBooking);
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
        return BookingMapper.bookingToBookingItemDto(nextBooking);
    }
}
