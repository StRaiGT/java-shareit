package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentDto;
import ru.practicum.shareit.item.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemExtendedDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    public abstract ItemDto toItemDto(Item item);

    @Mapping(target = "id", expression = "java(itemDto.getId())")
    @Mapping(target = "name", expression = "java(itemDto.getName())")
    @Mapping(target = "owner", expression = "java(user)")
    public abstract Item toItem(ItemDto itemDto, User user);

    @Mapping(target = "id", expression = "java(item.getId())")
    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    @Mapping(target = "lastBooking", expression = "java(lastBooking)")
    @Mapping(target = "nextBooking", expression = "java(nextBooking)")
    @Mapping(target = "comments", expression = "java(commentToCommentDto(item.getComments()))")
    public abstract ItemExtendedDto toItemExtendedDto(Item item, BookingItemDto lastBooking, BookingItemDto nextBooking);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    public abstract BookingItemDto bookingToBookingItemDto(Booking booking);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "createdDate", expression = "java(dateTime)")
    @Mapping(target = "author", expression = "java(user)")
    public abstract Comment commentRequestDtoToComment(CommentRequestDto commentRequestDto, LocalDateTime dateTime,
                                                       User user, Long itemId);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    public abstract CommentDto commentToCommentDto(Comment comment);

    protected List<CommentDto> commentToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(this::commentToCommentDto)
                .collect(Collectors.toList());
    }
}
