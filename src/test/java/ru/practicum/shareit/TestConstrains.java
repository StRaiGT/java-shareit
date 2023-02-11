package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public abstract class TestConstrains {
    public static User getUser1() {
        return User.builder()
                .id(1L)
                .name("Test user 1")
                .email("tester1@yandex.ru")
                .build();
    }

    public static User getUser2() {
        return User.builder()
                .id(2L)
                .name("Test user 2")
                .email("tester2@yandex.ru")
                .build();
    }

    public static User getUser3() {
        return User.builder()
                .id(3L)
                .name("Test user 3")
                .email("tester3@yandex.ru")
                .build();
    }

    public static UserDto getUser1Dto() {
        return UserDto.builder()
                .id(1L)
                .name("Test user 1")
                .email("tester1@yandex.ru")
                .build();
    }

    public static UserDto getUser2Dto() {
        return UserDto.builder()
                .id(2L)
                .name("Test user 2")
                .email("tester2@yandex.ru")
                .build();
    }

    public static UserDto getPatchUser1Dto() {
        return UserDto.builder()
                .id(1L)
                .name("Patch test user 1")
                .email("tester2@yandex.ru")
                .build();
    }

    public static Item getItem1(User user) {
        return Item.builder()
                .id(1L)
                .name("item1 name")
                .description("seaRch1 description ")
                .available(true)
                .owner(user)
                .build();
    }

    public static Item getItem2(User user) {
        return Item.builder()
                .id(2L)
                .name("item2 name")
                .description("SeARch1 description")
                .available(true)
                .owner(user)
                .build();
    }

    public static Item getItem3(User user) {
        return Item.builder()
                .id(3L)
                .name("item3 name")
                .description("itEm3 description")
                .available(false)
                .owner(user)
                .build();
    }

    public static ItemDto getItem1Dto(User user) {
        return ItemDto.builder()
                .id(1L)
                .name("item1 name")
                .description("seaRch1 description ")
                .available(true)
                .ownerId(user.getId())
                .build();
    }

    public static ItemDto getPatchItem1Dto() {
        return ItemDto.builder()
                .id(1L)
                .name("Patch item1 name")
                .description("Patch seaRch1 description")
                .available(false)
                .build();
    }

    public static Item getItem1WithComments(User user, Comment comment1, Comment comment2) {
        return Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .owner(user)
                .requestId(1L)
                .comments(List.of(comment1, comment2))
                .build();
    }

    public static ItemDto getItem1WithCommentsDto(User user) {
        return ItemDto.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .ownerId(user.getId())
                .requestId(1L)
                .build();
    }

    public static Item getItem1WithRequest(User user) {
        return Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .owner(user)
                .requestId(1L)
                .build();
    }

    public static ItemRequestCreateDto getItem1RequestCreateDto() {
        return ItemRequestCreateDto.builder()
                .description("item description")
                .build();
    }

    public static Comment getComment1(User user, LocalDateTime dateTime) {
        return Comment.builder()
                .id(1L)
                .text("comment1 text")
                .createdDate(dateTime)
                .author(user)
                .itemId(1L)
                .build();
    }

    public static Comment getComment2(User user, LocalDateTime dateTime) {
        return Comment.builder()
                .id(2L)
                .text("comment2 text")
                .createdDate(dateTime)
                .author(user)
                .itemId(1L)
                .build();
    }

    public static CommentRequestDto getCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("commentRequestDto text")
                .build();
    }

    public static Booking getBooking1(User user, Item item, LocalDateTime dateTime) {
        return Booking.builder()
                .id(1L)
                .start(dateTime.minusYears(10))
                .end(dateTime.minusYears(9))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    public static Booking getBooking2(User user, Item item, LocalDateTime dateTime) {
        return Booking.builder()
                .id(2L)
                .start(dateTime.minusYears(5))
                .end(dateTime.plusYears(5))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    public static Booking getBooking3(User user, Item item, LocalDateTime dateTime) {
        return Booking.builder()
                .id(3L)
                .start(dateTime.plusYears(8))
                .end(dateTime.plusYears(9))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    public static Booking getBooking4(User user, Item item, LocalDateTime dateTime) {
        return Booking.builder()
                .id(4L)
                .start(dateTime.plusYears(9))
                .end(dateTime.plusYears(10))
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();
    }

    public static BookingResponseDto getBooking1ResponseDto(UserDto userDto, ItemDto itemDto, LocalDateTime dateTime) {
        return BookingResponseDto.builder()
                .id(1L)
                .start(dateTime.minusYears(10))
                .end(dateTime.minusYears(9))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
    }

    public static BookingItemDto getLastBooking1(User user, LocalDateTime dateTime) {
        return BookingItemDto.builder()
                .id(1L)
                .bookerId(user.getId())
                .start(dateTime)
                .end(dateTime.plusHours(1))
                .build();
    }

    public static BookingItemDto getNextBooking2(User user, LocalDateTime dateTime) {
        return BookingItemDto.builder()
                .id(2L)
                .bookerId(user.getId())
                .start(dateTime.plusHours(2))
                .end(dateTime.plusHours(3))
                .build();
    }

    public static BookingRequestDto getBookingRequestDto(LocalDateTime dateTime, Item item) {
        return BookingRequestDto.builder()
                .start(dateTime.plusYears(5))
                .end(dateTime.plusYears(6))
                .itemId(item.getId())
                .build();
    }

    public static BookingRequestDto getBookingRequestDtoWrongDate(LocalDateTime dateTime, Item item) {
        return BookingRequestDto.builder()
                .start(dateTime.plusYears(5))
                .end(dateTime)
                .itemId(item.getId())
                .build();
    }

    public static ItemRequest getItemRequest1(User user, LocalDateTime dateTime, List<Item> items) {
        return ItemRequest.builder()
                .id(1L)
                .description("itemRequest1 description")
                .requesterId(user)
                .created(dateTime)
                .items(items)
                .build();
    }

    public static LocalDateTime getDateTime() {
        return LocalDateTime.of(2023,1,1,10,0,0);
    }

    public static Pageable getPageable() {
        final int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        final int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        return PageRequest.of(from / size, size);
    }
}
