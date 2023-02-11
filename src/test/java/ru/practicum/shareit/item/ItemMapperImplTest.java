package ru.practicum.shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestConstrains;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentDto;
import ru.practicum.shareit.item.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemExtendedDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperImplTest {
    @InjectMocks
    private ItemMapperImpl itemMapper;

    private final User user = TestConstrains.getUser1();
    private final CommentRequestDto commentRequestDto = TestConstrains.getCommentRequestDto();
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final Comment comment1 = TestConstrains.getComment1(user, dateTime);
    private final Comment comment2 = TestConstrains.getComment2(user, dateTime);
    private final Item item = TestConstrains.getItem1WithComments(user, comment1, comment2);
    private final ItemDto itemDto = TestConstrains.getItem1WithCommentsDto(user);
    private final Booking booking = TestConstrains.getBooking1(user, item,dateTime);
    private final BookingItemDto lastBooking = TestConstrains.getLastBooking1(user, dateTime);
    private final BookingItemDto nextBooking = TestConstrains.getNextBooking2(user, dateTime);

    @Nested
    class ToItemDto {
        @Test
        void shouldReturnItemDto() {
            ItemDto result = itemMapper.toItemDto(item);

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(item.getAvailable(), result.getAvailable());
            assertEquals(item.getOwner().getId(), result.getOwnerId());
            assertEquals(item.getRequestId(), result.getRequestId());
        }

        @Test
        void shouldReturnNull() {
            ItemDto result = itemMapper.toItemDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToItem {
        @Test
        void shouldReturnItemDto() {
            Item result = itemMapper.toItem(itemDto, user);

            assertEquals(itemDto.getId(), result.getId());
            assertEquals(itemDto.getName(), result.getName());
            assertEquals(itemDto.getDescription(), result.getDescription());
            assertEquals(itemDto.getAvailable(), result.getAvailable());
            assertEquals(itemDto.getOwnerId(), result.getOwner().getId());
            assertEquals(user.getName(), result.getOwner().getName());
            assertEquals(user.getEmail(), result.getOwner().getEmail());
            assertEquals(itemDto.getRequestId(), result.getRequestId());
            assertNull(result.getComments());
        }

        @Test
        void shouldReturnNull() {
            Item result = itemMapper.toItem(null, null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemExtendedDto {
        @Test
        void shouldReturnItemExtendedDto() {
            ItemExtendedDto result = itemMapper.toItemExtendedDto(item, lastBooking, nextBooking);

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(item.getAvailable(), result.getAvailable());
            assertEquals(item.getOwner().getId(), result.getOwnerId());
            assertEquals(item.getRequestId(), result.getRequestId());

            assertEquals(lastBooking.getId(), result.getLastBooking().getId());
            assertEquals(lastBooking.getBookerId(), result.getLastBooking().getBookerId());
            assertEquals(lastBooking.getStart(), result.getLastBooking().getStart());
            assertEquals(lastBooking.getEnd(), result.getLastBooking().getEnd());

            assertEquals(nextBooking.getId(), result.getNextBooking().getId());
            assertEquals(nextBooking.getBookerId(), result.getNextBooking().getBookerId());
            assertEquals(nextBooking.getStart(), result.getNextBooking().getStart());
            assertEquals(nextBooking.getEnd(), result.getNextBooking().getEnd());

            assertEquals(item.getComments().size(), result.getComments().size());

            assertEquals(item.getComments().get(0).getId(), result.getComments().get(0).getId());
            assertEquals(item.getComments().get(0).getText(), result.getComments().get(0).getText());
            assertEquals(item.getComments().get(0).getCreatedDate(), result.getComments().get(0).getCreatedDate());
            assertEquals(item.getComments().get(0).getAuthor().getName(), result.getComments().get(0).getAuthorName());

            assertEquals(item.getComments().get(1).getId(), result.getComments().get(1).getId());
            assertEquals(item.getComments().get(1).getText(), result.getComments().get(1).getText());
            assertEquals(item.getComments().get(1).getCreatedDate(), result.getComments().get(1).getCreatedDate());
            assertEquals(item.getComments().get(1).getAuthor().getName(), result.getComments().get(1).getAuthorName());
        }

        @Test
        void shouldReturnNull() {
            ItemExtendedDto result = itemMapper.toItemExtendedDto(null, null, null);

            assertNull(result);
        }
    }

    @Nested
    class BookingToBookingItemDto {
        @Test
        void shouldReturnBookingItemDto() {
            BookingItemDto result = itemMapper.bookingToBookingItemDto(booking);

            assertEquals(booking.getId(), result.getId());
            assertEquals(booking.getBooker().getId(), result.getBookerId());
            assertEquals(booking.getStart(), result.getStart());
            assertEquals(booking.getEnd(), result.getEnd());
        }

        @Test
        void shouldReturnNull() {
            BookingItemDto result = itemMapper.bookingToBookingItemDto(null);

            assertNull(result);
        }
    }

    @Nested
    class CommentRequestDtoToComment {
        @Test
        void shouldReturnComment() {
            Comment result = itemMapper.commentRequestDtoToComment(commentRequestDto, dateTime.plusHours(4),
                    user, item.getId());

            assertNull(result.getId());
            assertEquals(commentRequestDto.getText(), result.getText());
            assertEquals(dateTime.plusHours(4), result.getCreatedDate());
            assertEquals(user.getId(), result.getAuthor().getId());
            assertEquals(user.getName(), result.getAuthor().getName());
            assertEquals(user.getEmail(), result.getAuthor().getEmail());
            assertEquals(item.getId(), result.getItemId());
        }

        @Test
        void shouldReturnNull() {
            Comment result = itemMapper.commentRequestDtoToComment(null, null,
                    null, null);

            assertNull(result);
        }
    }

    @Nested
    class CommentToCommentDto {
        @Test
        void shouldReturnCommentDto() {
            CommentDto result = itemMapper.commentToCommentDto(comment1);

            assertEquals(comment1.getId(), result.getId());
            assertEquals(comment1.getText(), result.getText());
            assertEquals(comment1.getCreatedDate(), result.getCreatedDate());
            assertEquals(comment1.getAuthor().getName(), result.getAuthorName());
        }

        @Test
        void shouldReturnNull() {
            CommentDto result = itemMapper.commentToCommentDto(null);

            assertNull(result);
        }
    }
}
