package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingRequestDto;
import ru.practicum.shareit.booking.BookingResponseDto;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.CommentRequestDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemExtendedDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            List<ItemExtendedDto> itemsFromController = itemController.getByOwnerId(userDto.getId());

            assertEquals(itemsFromController.size(), 1);

            ItemExtendedDto itemFromController = itemsFromController.get(0);

            assertEquals(itemFromController.getId(), itemDto.getId());
            assertEquals(itemFromController.getName(), itemDto.getName());
            assertEquals(itemFromController.getDescription(), itemDto.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemOwnerIdNotFound() {
            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(10L)
                    .request(null)
                    .build();
            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.create(10L, itemDto));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        }
    }

    @Nested
    class GetByOwner {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto2.getOwnerId(), itemDto2);

            ItemDto itemDto3 = ItemDto.builder()
                    .id(3L)
                    .name("Test item 3")
                    .description("Test item description 3")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto3.getOwnerId(), itemDto3);

            List<ItemExtendedDto> itemsFromController1 = itemController.getByOwnerId(userDto1.getId());

            assertEquals(itemsFromController1.size(), 2);

            ItemExtendedDto itemFromController1 = itemsFromController1.get(0);
            ItemExtendedDto itemFromController3 = itemsFromController1.get(1);

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getName(), itemDto1.getName());
            assertEquals(itemFromController1.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController1.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController1.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController1.getRequest(), itemDto1.getRequest());

            assertEquals(itemFromController3.getId(), itemDto3.getId());
            assertEquals(itemFromController3.getName(), itemDto3.getName());
            assertEquals(itemFromController3.getDescription(), itemDto3.getDescription());
            assertEquals(itemFromController3.getAvailable(), itemDto3.getAvailable());
            assertEquals(itemFromController3.getOwnerId(), itemDto3.getOwnerId());
            assertEquals(itemFromController3.getRequest(), itemDto3.getRequest());

            List<ItemExtendedDto> itemsFromController2 = itemController.getByOwnerId(userDto2.getId());

            assertEquals(itemsFromController2.size(), 1);

            ItemExtendedDto itemFromController2 = itemsFromController2.get(0);

            assertEquals(itemFromController2.getId(), itemDto2.getId());
            assertEquals(itemFromController2.getName(), itemDto2.getName());
            assertEquals(itemFromController2.getDescription(), itemDto2.getDescription());
            assertEquals(itemFromController2.getAvailable(), itemDto2.getAvailable());
            assertEquals(itemFromController2.getOwnerId(), itemDto2.getOwnerId());
            assertEquals(itemFromController2.getRequest(), itemDto2.getRequest());
        }

        @Test
        public void shouldGetIfEmpty() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            List<ItemExtendedDto> itemsFromController = itemController.getByOwnerId(userDto.getId());

            assertEquals(itemsFromController.size(), 0);
        }

        @Test
        public void shouldHaveBookingDateAndComments() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto2.getOwnerId(), itemDto2);

            BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto1.getId())
                    .build();
            BookingResponseDto bookingResponseDto1 = bookingController.create(userDto2.getId(), bookingRequestDto1);
            bookingController.patch(userDto1.getId(), bookingResponseDto1.getId(), true);

            BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 3, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 4, 30, 11, 0, 0))
                    .itemId(itemDto1.getId())
                    .build();
            BookingResponseDto bookingResponseDto2 = bookingController.create(userDto2.getId(), bookingRequestDto2);
            bookingController.patch(userDto1.getId(), bookingResponseDto2.getId(), true);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();
            itemController.addComment(userDto2.getId(),itemDto1.getId(), commentRequestDto);

            List<ItemExtendedDto> itemsFromController = itemController.getByOwnerId(userDto1.getId());

            assertEquals(itemsFromController.size(), 2);

            ItemExtendedDto itemFromController1 = itemsFromController.get(0);
            ItemExtendedDto itemFromController2 = itemsFromController.get(1);

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getLastBooking().getId(), bookingResponseDto1.getId());
            assertEquals(itemFromController1.getLastBooking().getBookerId(), bookingResponseDto1.getBooker().getId());
            assertEquals(itemFromController1.getLastBooking().getStart(), bookingResponseDto1.getStart());
            assertEquals(itemFromController1.getLastBooking().getEnd(), bookingResponseDto1.getEnd());
            assertEquals(itemFromController1.getNextBooking().getId(), bookingResponseDto2.getId());
            assertEquals(itemFromController1.getNextBooking().getBookerId(), bookingResponseDto2.getBooker().getId());
            assertEquals(itemFromController1.getNextBooking().getStart(), bookingResponseDto2.getStart());
            assertEquals(itemFromController1.getNextBooking().getEnd(), bookingResponseDto2.getEnd());

            List<CommentDto> commentsItem1 = itemFromController1.getComments();

            assertEquals(commentsItem1.size(), 1);
            CommentDto commentDto = commentsItem1.get(0);

            assertEquals(commentDto.getText(), commentRequestDto.getText());
            assertEquals(commentDto.getAuthorName(), userDto2.getName());

            assertEquals(itemFromController2.getId(), itemDto2.getId());
            assertNull(itemFromController2.getLastBooking());
            assertNull(itemFromController2.getNextBooking());

            List<CommentDto> commentsItem2 = itemFromController2.getComments();

            assertEquals(commentsItem2.size(), 0);
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            ItemExtendedDto itemFromController = itemController.getById(userDto.getId(), itemDto.getId());

            assertEquals(itemFromController.getId(), itemDto.getId());
            assertEquals(itemFromController.getName(), itemDto.getName());
            assertEquals(itemFromController.getDescription(), itemDto.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemIdNotFound() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.getById(userDto.getId(), 10L));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
        }

        @Test
        public void shouldRequestByOwnerHaveBookingDateAndComments() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto2.getOwnerId(), itemDto2);

            BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto1.getId())
                    .build();
            BookingResponseDto bookingResponseDto1 = bookingController.create(userDto2.getId(), bookingRequestDto1);
            bookingController.patch(userDto1.getId(), bookingResponseDto1.getId(), true);

            BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 3, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 4, 30, 11, 0, 0))
                    .itemId(itemDto1.getId())
                    .build();
            BookingResponseDto bookingResponseDto2 = bookingController.create(userDto2.getId(), bookingRequestDto2);
            bookingController.patch(userDto1.getId(), bookingResponseDto2.getId(), true);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();
            itemController.addComment(userDto2.getId(),itemDto1.getId(), commentRequestDto);

            ItemExtendedDto itemFromController1 = itemController.getById(userDto1.getId(), itemDto1.getId());

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getLastBooking().getId(), bookingResponseDto1.getId());
            assertEquals(itemFromController1.getLastBooking().getBookerId(), bookingResponseDto1.getBooker().getId());
            assertEquals(itemFromController1.getLastBooking().getStart(), bookingResponseDto1.getStart());
            assertEquals(itemFromController1.getLastBooking().getEnd(), bookingResponseDto1.getEnd());
            assertEquals(itemFromController1.getNextBooking().getId(), bookingResponseDto2.getId());
            assertEquals(itemFromController1.getNextBooking().getBookerId(), bookingResponseDto2.getBooker().getId());
            assertEquals(itemFromController1.getNextBooking().getStart(), bookingResponseDto2.getStart());
            assertEquals(itemFromController1.getNextBooking().getEnd(), bookingResponseDto2.getEnd());

            List<CommentDto> commentsItem1 = itemFromController1.getComments();

            assertEquals(commentsItem1.size(), 1);
            CommentDto comment = commentsItem1.get(0);

            assertEquals(comment.getText(), commentRequestDto.getText());
            assertEquals(comment.getAuthorName(), userDto2.getName());

            ItemExtendedDto itemFromController2 = itemController.getById(userDto1.getId(), itemDto2.getId());

            assertEquals(itemFromController2.getId(), itemDto2.getId());
            assertNull(itemFromController2.getLastBooking());
            assertNull(itemFromController2.getNextBooking());

            List<CommentDto> commentsItem2 = itemFromController2.getComments();

            assertEquals(commentsItem2.size(), 0);
        }

        @Test
        public void shouldRequestByNoOwnerHaveNotBookingDateAndHaveComments() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto2.getOwnerId(), itemDto2);

            BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto1.getId())
                    .build();
            BookingResponseDto bookingResponseDto1 = bookingController.create(userDto2.getId(), bookingRequestDto1);
            bookingController.patch(userDto1.getId(), bookingResponseDto1.getId(), true);

            BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 3, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 4, 30, 11, 0, 0))
                    .itemId(itemDto1.getId())
                    .build();
            BookingResponseDto bookingResponseDto2 = bookingController.create(userDto2.getId(), bookingRequestDto2);
            bookingController.patch(userDto1.getId(), bookingResponseDto2.getId(), true);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();
            itemController.addComment(userDto2.getId(),itemDto1.getId(), commentRequestDto);

            ItemExtendedDto itemFromController1 = itemController.getById(userDto2.getId(), itemDto1.getId());

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertNull(itemFromController1.getLastBooking());
            assertNull(itemFromController1.getNextBooking());

            List<CommentDto> commentsItem1 = itemFromController1.getComments();

            assertEquals(commentsItem1.size(), 1);
            CommentDto comment = commentsItem1.get(0);

            assertEquals(comment.getText(), commentRequestDto.getText());
            assertEquals(comment.getAuthorName(), userDto2.getName());

            ItemExtendedDto itemFromController2 = itemController.getById(userDto2.getId(), itemDto2.getId());

            assertEquals(itemFromController2.getId(), itemDto2.getId());
            assertNull(itemFromController2.getLastBooking());
            assertNull(itemFromController2.getNextBooking());

            List<CommentDto> commentsItem2 = itemFromController2.getComments();

            assertEquals(commentsItem2.size(), 0);
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldPatch() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Patch test item 1")
                    .description("Patch test item description 1")
                    .available(false)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.patch(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2);

            ItemExtendedDto itemFromController = itemController.getById(userDto.getId(), itemDto1.getId());

            assertEquals(itemFromController.getId(), itemDto1.getId());
            assertEquals(itemFromController.getName(), itemDto2.getName());
            assertEquals(itemFromController.getDescription(), itemDto2.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto2.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto2.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto2.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemOwnerIdForbidden() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Patch test item 1")
                    .description("Patch test item description 1")
                    .available(false)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();

            ForbiddenException exception = assertThrows(ForbiddenException.class, () -> itemController.patch(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2));
            assertEquals("Изменение вещи доступно только владельцу.", exception.getMessage());

            ItemExtendedDto itemFromController = itemController.getById(userDto1.getId(), itemDto1.getId());

            assertEquals(itemFromController.getId(), itemDto1.getId());
            assertEquals(itemFromController.getName(), itemDto1.getName());
            assertEquals(itemFromController.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto1.getRequest());
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.create(userDto.getId(), itemDto);

            itemController.delete(itemDto.getId());

            assertEquals(itemController.getByOwnerId(userDto.getId()).size(), 0);
        }

        @Test
        public void shouldDeleteIfItemIdNotFound() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            assertThrows(EmptyResultDataAccessException.class, () -> itemController.delete(10L));

            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.getById(userDto.getId(), 10L));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldSearch() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1 SeCREt")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2 SeCREt")
                    .description("Test item description 2 SeCREt")
                    .available(false)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto2.getOwnerId(), itemDto2);

            ItemDto itemDto3 = ItemDto.builder()
                    .id(3L)
                    .name("Test item 3")
                    .description("Test item description 3 SeCREt")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto3.getOwnerId(), itemDto3);

            ItemDto itemDto4 = ItemDto.builder()
                    .id(4L)
                    .name("Test item 4")
                    .description("Test item description 4")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto4.getOwnerId(), itemDto4);

            List<ItemDto> itemsFromController = itemController.search("sEcrEt");

            assertEquals(itemsFromController.size(), 2);

            ItemDto itemFromController1 = itemsFromController.get(0);
            ItemDto itemFromController2 = itemsFromController.get(1);

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getName(), itemDto1.getName());
            assertEquals(itemFromController1.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController1.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController1.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController1.getRequest(), itemDto1.getRequest());

            assertEquals(itemFromController2.getId(), itemDto3.getId());
            assertEquals(itemFromController2.getName(), itemDto3.getName());
            assertEquals(itemFromController2.getDescription(), itemDto3.getDescription());
            assertEquals(itemFromController2.getAvailable(), itemDto3.getAvailable());
            assertEquals(itemFromController2.getOwnerId(), itemDto3.getOwnerId());
            assertEquals(itemFromController2.getRequest(), itemDto3.getRequest());
        }

        @Test
        public void shouldSearchIfEmpty() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            List<ItemDto> itemsFromController = itemController.search(" ");

            assertEquals(itemsFromController.size(), 0);
        }
    }

    @Nested
    class AddComment {
        @Test
        public void shouldCreate() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingController.create(userDto2.getId(), bookingRequestDto);
            bookingController.patch(userDto1.getId(), bookingResponseDto.getId(), true);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();
            itemController.addComment(userDto2.getId(),itemDto.getId(), commentRequestDto);

            ItemExtendedDto item = itemController.getById(userDto1.getId(), itemDto.getId());

            List<CommentDto> comments = item.getComments();

            assertEquals(comments.size(), 1);
            CommentDto comment = comments.get(0);

            assertEquals(comment.getText(), commentRequestDto.getText());
            assertEquals(comment.getAuthorName(), userDto2.getName());
        }

        @Test
        public void shouldThrowExceptionIfNoBooking() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemController.addComment(userDto2.getId(),itemDto.getId(), commentRequestDto));
            assertEquals("Пользователь не брал данную вещь в аренду.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfBookingNotFinished() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 3, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingController.create(userDto2.getId(), bookingRequestDto);
            bookingController.patch(userDto1.getId(), bookingResponseDto.getId(), true);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemController.addComment(userDto2.getId(),itemDto.getId(), commentRequestDto));
            assertEquals("Пользователь не брал данную вещь в аренду.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfBookingNotApproved() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            bookingController.create(userDto2.getId(), bookingRequestDto);

            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("comment")
                    .build();

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemController.addComment(userDto2.getId(),itemDto.getId(), commentRequestDto));
            assertEquals("Пользователь не брал данную вещь в аренду.", exception.getMessage());
        }
    }
}
