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
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "mapOwnerIdFromOwner")
    public abstract ItemDto toItemDto(Item item);

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "mapOwnerFromOwnerId")
    public abstract Item toItem(ItemDto itemDto);

    @Mapping(source = "owner", target = "ownerId", qualifiedByName = "mapOwnerIdFromOwner")
    @Mapping(target = "lastBooking", expression = "java(addLastBooking(item))")
    @Mapping(target = "nextBooking", expression = "java(addNextBooking(item))")
    @Mapping(target = "comments", expression = "java(addComment(item))")
    public abstract ItemExtendedDto toItemExtendedDto(Item item);

    @Mapping(source = "booker", target = "bookerId", qualifiedByName = "mapBookerIdFromBooker")
    public abstract BookingItemDto bookingToBookingItemDto(Booking booking);

    @Mapping(source = "authorId", target = "author", qualifiedByName = "mapAuthorFromAuthorId")
    @Mapping(source = "itemId", target = "item", qualifiedByName = "mapItemFromItemId")
    public abstract Comment commentRequestDtoToComment(CommentRequestDto commentRequestDto);

    @Mapping(source = "author", target = "authorName", qualifiedByName = "mapAuthorNameFromAuthor")
    public abstract CommentDto commentToCommentDto(Comment comment);

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

    @Named("addComment")
    @Transactional(readOnly = true)
    public List<CommentDto> addComment(Item item) {
        return commentRepository.findByItemId(item.getId()).stream()
                .map(this::commentToCommentDto)
                .collect(Collectors.toList());
    }

    @Named("mapBookerIdFromBooker")
    @Transactional(readOnly = true)
    Long mapBookerIdFromBooker(User user) {
        return user.getId();
    }

    @Named("mapAuthorFromAuthorId")
    @Transactional(readOnly = true)
    User mapAuthorFromAuthorId(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));
    }

    @Named("mapItemFromItemId")
    @Transactional(readOnly = true)
    Item mapItemFromItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует."));
    }

    @Named("mapAuthorNameFromAuthor")
    @Transactional(readOnly = true)
    String mapAuthorNameFromAuthor(User author) {
        return author.getName();
    }
}
