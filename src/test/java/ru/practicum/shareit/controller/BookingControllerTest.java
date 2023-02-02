package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTest {
    private final UserController userController;
    private final ItemController itemController;
    private final BookingController bookingController;

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingController.create(userDto2.getId(), bookingRequestDto);

            assertEquals(bookingResponseDto.getId(), 1L);
            assertEquals(bookingResponseDto.getStatus(), Status.WAITING);
            assertEquals(bookingResponseDto.getStart(), bookingRequestDto.getStart());
            assertEquals(bookingResponseDto.getEnd(), bookingRequestDto.getEnd());
            assertEquals(bookingResponseDto.getItem().getId(), itemDto.getId());
            assertEquals(bookingResponseDto.getItem().getName(), itemDto.getName());
            assertEquals(bookingResponseDto.getItem().getDescription(), itemDto.getDescription());
            assertEquals(bookingResponseDto.getItem().getAvailable(), itemDto.getAvailable());
            assertEquals(bookingResponseDto.getItem().getOwnerId(), itemDto.getOwnerId());
            assertEquals(bookingResponseDto.getBooker().getId(), userDto2.getId());
            assertEquals(bookingResponseDto.getBooker().getName(), userDto2.getName());
            assertEquals(bookingResponseDto.getBooker().getEmail(), userDto2.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(false)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingController.create(100L, bookingRequestDto));
            assertEquals("Предмет недоступен для бронирования.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfItemIdNotFound() {
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

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(100L)
                    .build();

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.create(userDto2.getId(), bookingRequestDto));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfItemNotAvailable() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(false)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingController.create(userDto2.getId(), bookingRequestDto));
            assertEquals("Предмет недоступен для бронирования.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfBookingByOwner() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
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

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.create(userDto.getId(), bookingRequestDto));
            assertEquals("Владелец не может бронировать собственную вещь.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfEndIsAfterStart() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 3, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingController.create(userDto2.getId(), bookingRequestDto));
            assertEquals("Недопустимое время брони.", exception.getMessage());
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldPatch() {
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

            BookingRequestDto bookingRequestDto2 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto2.getId())
                    .build();
            BookingResponseDto bookingResponseDto2 = bookingController.create(userDto2.getId(), bookingRequestDto2);

            bookingController.patch(userDto1.getId(), bookingResponseDto1.getId(), true);
            bookingController.patch(userDto1.getId(), bookingResponseDto2.getId(), false);

            BookingResponseDto booking1 = bookingController.getById(userDto1.getId(), bookingResponseDto1.getId());
            BookingResponseDto booking2 = bookingController.getById(userDto1.getId(), bookingResponseDto2.getId());

            assertEquals(booking1.getId(), 1L);
            assertEquals(booking1.getStatus(), Status.APPROVED);
            assertEquals(booking2.getId(), 2L);
            assertEquals(booking2.getStatus(), Status.REJECTED);
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingController.create(userDto2.getId(), bookingRequestDto);

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.patch(100L, bookingResponseDto.getId(), true));
            assertEquals("Изменение статуса бронирования доступно только владельцу.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfItemIdNotFound() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            bookingController.create(userDto2.getId(), bookingRequestDto);

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.patch(userDto1.getId(), 100L, true));
            assertEquals("Бронирование с таким id не существует.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfPatchNotOwner() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            bookingController.create(userDto2.getId(), bookingRequestDto1);

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.patch(userDto2.getId(),itemDto.getId(), true));
            assertEquals("Изменение статуса бронирования доступно только владельцу.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionIfPatchTwice() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto1 = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            bookingController.create(userDto2.getId(), bookingRequestDto1);

            bookingController.patch(userDto1.getId(),itemDto.getId(), true);

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingController.patch(userDto1.getId(),itemDto.getId(), true));
            assertEquals("Ответ по бронированию уже дан.", exception.getMessage());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGetByAuthorOrOwner() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingController.create(userDto2.getId(), bookingRequestDto);

            BookingResponseDto bookingGetByController1 = bookingController.getById(userDto2.getId(), bookingResponseDto.getId());

            assertEquals(bookingGetByController1.getId(), 1L);
            assertEquals(bookingGetByController1.getStatus(), Status.WAITING);
            assertEquals(bookingGetByController1.getStart(), bookingRequestDto.getStart());
            assertEquals(bookingGetByController1.getEnd(), bookingRequestDto.getEnd());
            assertEquals(bookingGetByController1.getItem().getId(), itemDto.getId());
            assertEquals(bookingGetByController1.getItem().getName(), itemDto.getName());
            assertEquals(bookingGetByController1.getItem().getDescription(), itemDto.getDescription());
            assertEquals(bookingGetByController1.getItem().getAvailable(), itemDto.getAvailable());
            assertEquals(bookingGetByController1.getItem().getOwnerId(), itemDto.getOwnerId());
            assertEquals(bookingGetByController1.getBooker().getId(), userDto2.getId());
            assertEquals(bookingGetByController1.getBooker().getName(), userDto2.getName());
            assertEquals(bookingGetByController1.getBooker().getEmail(), userDto2.getEmail());

            BookingResponseDto bookingGetByController2 = bookingController.getById(userDto1.getId(), bookingResponseDto.getId());

            assertEquals(bookingGetByController2.getId(), 1L);
            assertEquals(bookingGetByController2.getStatus(), Status.WAITING);
            assertEquals(bookingGetByController2.getStart(), bookingRequestDto.getStart());
            assertEquals(bookingGetByController2.getEnd(), bookingRequestDto.getEnd());
            assertEquals(bookingGetByController2.getItem().getId(), itemDto.getId());
            assertEquals(bookingGetByController2.getItem().getName(), itemDto.getName());
            assertEquals(bookingGetByController2.getItem().getDescription(), itemDto.getDescription());
            assertEquals(bookingGetByController2.getItem().getAvailable(), itemDto.getAvailable());
            assertEquals(bookingGetByController2.getItem().getOwnerId(), itemDto.getOwnerId());
            assertEquals(bookingGetByController2.getBooker().getId(), userDto2.getId());
            assertEquals(bookingGetByController2.getBooker().getName(), userDto2.getName());
            assertEquals(bookingGetByController2.getBooker().getEmail(), userDto2.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfBookingNotFound() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto);


            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.getById(userDto.getId(), 100L));
            assertEquals("Бронирование с таким id не существует.", exception.getMessage());

        }

        @Test
        public void shouldThrowExceptionIfRequestByNotAuthorOrOwner() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDto = bookingController.create(userDto2.getId(), bookingRequestDto);


            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.getById(100L, bookingResponseDto.getId()));
            assertEquals("Просмотр бронирования доступно только автору или владельцу.", exception.getMessage());
        }
    }

    @Nested
    class GetAllByByBookerId {
        @BeforeEach
        public void addBookings() {
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

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            BookingRequestDto bookingRequestDtoPast = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 29, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 29, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDtoPast = bookingController.create(userDto2.getId(), bookingRequestDtoPast);
            bookingController.patch(userDto1.getId(), bookingResponseDtoPast.getId(), true);

            BookingRequestDto bookingRequestDtoCurrent = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 7, 30, 10, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDtoCurrent = bookingController.create(userDto2.getId(), bookingRequestDtoCurrent);
            bookingController.patch(userDto1.getId(), bookingResponseDtoCurrent.getId(), true);

            BookingRequestDto bookingRequestDtoFuture = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 7, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 7, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            bookingController.create(userDto2.getId(), bookingRequestDtoFuture);

            BookingRequestDto bookingRequestDtoRejected = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 8, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 8, 30, 11, 0, 0))
                    .itemId(itemDto.getId())
                    .build();
            BookingResponseDto bookingResponseDtoRejected = bookingController.create(userDto2.getId(), bookingRequestDtoRejected);
            bookingController.patch(userDto1.getId(), bookingResponseDtoRejected.getId(), false);
        }

        @Test
        public void shouldGetByStateAll() {
            List<BookingResponseDto> bookings = bookingController.getAllByBookerId(2L, "ALL");

            assertEquals(bookings.size(), 4);
            assertEquals(bookings.get(0).getId(), 4L);
            assertEquals(bookings.get(1).getId(), 3L);
            assertEquals(bookings.get(2).getId(), 2L);
            assertEquals(bookings.get(3).getId(), 1L);
        }

        @Test
        public void shouldGetByStateCurrent() {
            List<BookingResponseDto> bookings = bookingController.getAllByBookerId(2L, "CURRENT");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getId(), 2L);
        }

        @Test
        public void shouldGetByStatePast() {
            List<BookingResponseDto> bookings = bookingController.getAllByBookerId(2L, "PAST");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getId(), 1L);
        }

        @Test
        public void shouldGetByStateFuture() {
            List<BookingResponseDto> bookings = bookingController.getAllByBookerId(2L, "FUTURE");

            assertEquals(bookings.size(), 2);
            assertEquals(bookings.get(0).getId(), 4L);
            assertEquals(bookings.get(1).getId(), 3L);
        }

        @Test
        public void shouldGetByStateWaiting() {
            List<BookingResponseDto> bookings = bookingController.getAllByBookerId(2L, "WAITING");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getId(), 3L);
        }

        @Test
        public void shouldGetByStateRejected() {
            List<BookingResponseDto> bookings = bookingController.getAllByBookerId(2L, "REJECTED");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getId(), 4L);
        }

        @Test
        public void shouldThrowExceptionIfBookerNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.getAllByBookerId(100L, "ALL"));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWithUnknownState() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> bookingController.getAllByBookerId(2L, "UNKNOWN"));
            assertEquals("Unknown state: UNKNOWN", exception.getMessage());
        }
    }

    @Nested
    class GetAllByByOwnerId {
        @BeforeEach
        public void addBookings() {
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

            ItemDto itemDtoPast = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDtoPast.getOwnerId(), itemDtoPast);

            BookingRequestDto bookingRequestDtoPast = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 29, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 1, 29, 11, 0, 0))
                    .itemId(itemDtoPast.getId())
                    .build();
            BookingResponseDto bookingResponseDtoPast = bookingController.create(userDto2.getId(), bookingRequestDtoPast);
            bookingController.patch(userDto1.getId(), bookingResponseDtoPast.getId(), true);

            ItemDto itemDtoCurrent = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDtoCurrent.getOwnerId(), itemDtoCurrent);

            BookingRequestDto bookingRequestDtoCurrent = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 1, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 7, 30, 10, 0, 0))
                    .itemId(itemDtoCurrent.getId())
                    .build();
            BookingResponseDto bookingResponseDtoCurrent = bookingController.create(userDto2.getId(), bookingRequestDtoCurrent);
            bookingController.patch(userDto1.getId(), bookingResponseDtoCurrent.getId(), true);

            ItemDto itemDtoFuture = ItemDto.builder()
                    .id(3L)
                    .name("Test item 3")
                    .description("Test item description 3")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDtoFuture.getOwnerId(), itemDtoFuture);

            BookingRequestDto bookingRequestDtoFuture = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 7, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 7, 30, 11, 0, 0))
                    .itemId(itemDtoFuture.getId())
                    .build();
            bookingController.create(userDto2.getId(), bookingRequestDtoFuture);

            ItemDto itemDtoRejected = ItemDto.builder()
                    .id(4L)
                    .name("Test item 4")
                    .description("Test item description 4")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.create(itemDtoRejected.getOwnerId(), itemDtoRejected);

            BookingRequestDto bookingRequestDtoRejected = BookingRequestDto.builder()
                    .start(LocalDateTime.of(2023, 8, 30, 10, 0, 0))
                    .end(LocalDateTime.of(2023, 8, 30, 11, 0, 0))
                    .itemId(itemDtoRejected.getId())
                    .build();
            BookingResponseDto bookingResponseDtoRejected = bookingController.create(userDto2.getId(), bookingRequestDtoRejected);
            bookingController.patch(userDto1.getId(), bookingResponseDtoRejected.getId(), false);
        }

        @Test
        public void shouldGetByStateAll() {
            List<BookingResponseDto> bookings = bookingController.getAllByOwnerId(1L, "ALL");

            assertEquals(bookings.size(), 4);
            assertEquals(bookings.get(0).getItem().getId(), 4L);
            assertEquals(bookings.get(1).getItem().getId(), 3L);
            assertEquals(bookings.get(2).getItem().getId(), 2L);
            assertEquals(bookings.get(3).getItem().getId(), 1L);
        }

        @Test
        public void shouldGetByStateCurrent() {
            List<BookingResponseDto> bookings = bookingController.getAllByOwnerId(1L, "CURRENT");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getItem().getId(), 2L);
        }

        @Test
        public void shouldGetByStatePast() {
            List<BookingResponseDto> bookings = bookingController.getAllByOwnerId(1L, "PAST");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getItem().getId(), 1L);
        }

        @Test
        public void shouldGetByStateFuture() {
            List<BookingResponseDto> bookings = bookingController.getAllByOwnerId(1L, "FUTURE");

            assertEquals(bookings.size(), 2);
            assertEquals(bookings.get(0).getItem().getId(), 4L);
            assertEquals(bookings.get(1).getItem().getId(), 3L);
        }

        @Test
        public void shouldGetByStateWaiting() {
            List<BookingResponseDto> bookings = bookingController.getAllByOwnerId(1L, "WAITING");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getItem().getId(), 3L);
        }

        @Test
        public void shouldGetByStateRejected() {
            List<BookingResponseDto> bookings = bookingController.getAllByOwnerId(1L, "REJECTED");

            assertEquals(bookings.size(), 1);
            assertEquals(bookings.get(0).getItem().getId(), 4L);
        }

        @Test
        public void shouldThrowExceptionIfOwnerNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingController.getAllByOwnerId(100L, "ALL"));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWithUnknownState() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> bookingController.getAllByOwnerId(1L, "UNKNOWN"));
            assertEquals("Unknown state: UNKNOWN", exception.getMessage());
        }
    }
}
