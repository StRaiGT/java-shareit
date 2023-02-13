package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private static User user1;
    private static User user2;
    private static UserDto userDto2;
    private static ItemDto itemDto;
    private static BookingResponseDto bookingResponseDto1;
    private static BookingResponseDto bookingResponseDto2;

    @BeforeAll
    public static void beforeAll() {
        user1 = User.builder()
                .id(1L)
                .name("Test user 1")
                .email("tester1@yandex.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Test user 2")
                .email("tester2@yandex.ru")
                .build();

        userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("item dto 1")
                .description("item dto 1 description")
                .available(true)
                .ownerId(user1.getId())
                .requestId(1L)
                .build();

        bookingResponseDto1 = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .item(itemDto)
                .booker(userDto2)
                .status(Status.WAITING)
                .build();

        bookingResponseDto2 = BookingResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(20))
                .item(itemDto)
                .booker(userDto2)
                .status(Status.WAITING)
                .build();
    }

    @Nested
    class Create {
        private BookingRequestDto bookingRequestDto;

        @BeforeEach
        public void beforeEachCreate() {
            bookingRequestDto = BookingRequestDto.builder()
                    .start(LocalDateTime.now().plusMinutes(5))
                    .end(LocalDateTime.now().plusMinutes(10))
                    .itemId(1L)
                    .build();
        }

        @Test
        public void shouldCreate() throws Exception {
            when(bookingService.create(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.any(BookingRequestDto.class)))
                    .thenReturn(bookingResponseDto1);

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, user2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(bookingResponseDto1)));

            verify(bookingService, times(1)).create(ArgumentMatchers.eq(user2.getId()),
                    ArgumentMatchers.any(BookingRequestDto.class));
        }

        @Test
        public void shouldThrowExceptionIfStartInPast() throws Exception {
            bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(5));
            bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(10));

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, user2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfEndInPresent() throws Exception {
            bookingRequestDto.setStart(LocalDateTime.now().plusMinutes(5));
            bookingRequestDto.setEnd(LocalDateTime.now());

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, user2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfItemIdIsNull() throws Exception {
            bookingRequestDto.setItemId(null);

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, user2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class Patch {
        private BookingResponseDto bookingResponseDto;

        @BeforeEach
        public void beforeEachPatch() {
            bookingResponseDto = BookingResponseDto.builder()
                    .id(1L)
                    .start(LocalDateTime.now().plusMinutes(5))
                    .end(LocalDateTime.now().plusMinutes(10))
                    .item(itemDto)
                    .booker(userDto2)
                    .status(Status.WAITING)
                    .build();
        }

        @Test
        public void shouldApproved() throws Exception {
            bookingResponseDto.setStatus(Status.APPROVED);

            when(bookingService.patch(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(bookingResponseDto.getId()),
                    ArgumentMatchers.eq(true)))
                    .thenReturn(bookingResponseDto);

            mvc.perform(patch("/bookings/{id}?approved={approved}", bookingResponseDto.getId(), true)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(bookingResponseDto)));

            verify(bookingService, times(1)).patch(ArgumentMatchers.eq(user2.getId()),
                    ArgumentMatchers.eq(bookingResponseDto.getId()), ArgumentMatchers.eq(true));
        }

        @Test
        public void shouldReject() throws Exception {
            bookingResponseDto.setStatus(Status.REJECTED);

            when(bookingService.patch(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(bookingResponseDto.getId()),
                    ArgumentMatchers.eq(false)))
                    .thenReturn(bookingResponseDto);

            mvc.perform(patch("/bookings/{id}?approved={approved}", bookingResponseDto.getId(), false)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(bookingResponseDto)));

            verify(bookingService, times(1)).patch(ArgumentMatchers.eq(user2.getId()),
                    ArgumentMatchers.eq(bookingResponseDto.getId()), ArgumentMatchers.eq(false));
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() throws Exception {
            when(bookingService.getById(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(bookingResponseDto1.getId())))
                    .thenReturn(bookingResponseDto1);

            mvc.perform(get("/bookings/{id}", bookingResponseDto1.getId())
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(bookingResponseDto1)));

            verify(bookingService, times(1))
                    .getById(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(bookingResponseDto1.getId()));
        }
    }

    @Nested
    class GetAllByByBookerId {
        private int from;
        private int size;

        @BeforeEach
        public void beforeEachGetAllByBookerId() {
            from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
            size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        }

        @Test
        public void shouldGetWithValidState() throws Exception {
            when(bookingService.getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(bookingResponseDto1, bookingResponseDto2));

            mvc.perform(get("/bookings?state={state}&from={from}&size={size}", "All", from, size)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(bookingResponseDto1, bookingResponseDto2))));

            verify(bookingService, times(1))
                    .getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldGetWithDefaultState() throws Exception {
            when(bookingService.getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(bookingResponseDto1, bookingResponseDto2));

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(bookingResponseDto1, bookingResponseDto2))));

            verify(bookingService, times(1))
                    .getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(State.ALL),
                            ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldThrowExceptionIfUnknownState() throws Exception {
            mvc.perform(get("/bookings?state={state}&from={from}&size={size}", "unknown", from, size)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingService, never())
                    .getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingService, never())
                    .getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingService, never())
                    .getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetAllByByOwnerId {
        private int from;
        private int size;

        @BeforeEach
        public void beforeEachGetAllByByOwnerId() {
            from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
            size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        }

        @Test
        public void shouldGetWithValidState() throws Exception {
            when(bookingService.getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(bookingResponseDto1, bookingResponseDto2));

            mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "All", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(bookingResponseDto1, bookingResponseDto2))));

            verify(bookingService, times(1))
                    .getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()), ArgumentMatchers.eq(State.ALL),
                            ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldGetWithDefaultState() throws Exception {
            when(bookingService.getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(bookingResponseDto1, bookingResponseDto2));

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(bookingResponseDto1, bookingResponseDto2))));

            verify(bookingService, times(1))
                    .getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()), ArgumentMatchers.eq(State.ALL),
                            ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldThrowExceptionIfUnknownState() throws Exception {
            mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "unknown", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingService, never())
                    .getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingService, never())
                    .getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingService, never())
                    .getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }
}