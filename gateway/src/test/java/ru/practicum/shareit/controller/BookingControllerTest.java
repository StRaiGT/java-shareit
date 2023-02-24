package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private BookingClient bookingClient;

    private final UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@yandex.ru")
            .build();
    private final UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("Test user 2")
            .email("tester2@yandex.ru")
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item dto 1")
            .description("item dto 1 description")
            .available(true)
            .ownerId(userDto1.getId())
            .requestId(1L)
            .build();
    private BookingRequestDto bookingRequestDto;
    private int from;
    private int size;

    @BeforeEach
    public void beforeEach() {
        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(1L)
                .build();
        from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() throws Exception {
            when(bookingClient.create(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.any(BookingRequestDto.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, userDto2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).create(ArgumentMatchers.eq(userDto2.getId()),
                    ArgumentMatchers.any(BookingRequestDto.class));
        }

        @Test
        public void shouldThrowExceptionIfStartInPast() throws Exception {
            bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(5));
            bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(10));

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, userDto2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfEndInPresent() throws Exception {
            bookingRequestDto.setStart(LocalDateTime.now().plusMinutes(5));
            bookingRequestDto.setEnd(LocalDateTime.now());

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, userDto2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfEndIsBeforeStart() throws Exception {
            bookingRequestDto.setStart(LocalDateTime.now().plusMinutes(10));
            bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(5));

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, userDto2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfItemIdIsNull() throws Exception {
            bookingRequestDto.setItemId(null);

            mvc.perform(post("/bookings")
                            .header(UserController.headerUserId, userDto2.getId())
                            .content(mapper.writeValueAsString(bookingRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(bookingClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldApproved() throws Exception {
            when(bookingClient.patch(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(99L),
                    ArgumentMatchers.eq(true))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(patch("/bookings/{id}?approved={approved}", 99L, true)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).patch(ArgumentMatchers.eq(userDto2.getId()),
                    ArgumentMatchers.eq(99L), ArgumentMatchers.eq(true));
        }

        @Test
        public void shouldReject() throws Exception {
            when(bookingClient.patch(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(99L),
                    ArgumentMatchers.eq(false))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(patch("/bookings/{id}?approved={approved}", 99L, false)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).patch(ArgumentMatchers.eq(userDto2.getId()),
                    ArgumentMatchers.eq(99L), ArgumentMatchers.eq(false));
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() throws Exception {
            when(bookingClient.getById(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(99L)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/bookings/{id}", 99L)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1))
                    .getById(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(99L));
        }
    }

    @Nested
    class GetAllByByBookerId {
        @Test
        public void shouldGetWithValidState() throws Exception {
            when(bookingClient.getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(from), ArgumentMatchers.eq(size))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/bookings?state={state}&from={from}&size={size}", "All", from, size)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()),
                    ArgumentMatchers.eq(State.ALL), ArgumentMatchers.eq(from), ArgumentMatchers.eq(size));
        }

        @Test
        public void shouldGetWithDefaultState() throws Exception {
            when(bookingClient.getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(from), ArgumentMatchers.eq(size))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).getAllByBookerId(ArgumentMatchers.eq(userDto2.getId()),
                    ArgumentMatchers.eq(State.ALL), ArgumentMatchers.eq(from), ArgumentMatchers.eq(size));
        }

        @Test
        public void shouldThrowExceptionIfUnknownState() throws Exception {
            mvc.perform(get("/bookings?state={state}&from={from}&size={size}", "unknown", from, size)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsNegative() throws Exception {
            size = -1;

            mvc.perform(get("/bookings?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto2.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByBookerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetAllByByOwnerId {
        @Test
        public void shouldGetWithValidState() throws Exception {
            when(bookingClient.getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(from), ArgumentMatchers.eq(size))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "All", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()),
                    ArgumentMatchers.eq(State.ALL), ArgumentMatchers.eq(from), ArgumentMatchers.eq(size));
        }

        @Test
        public void shouldGetWithDefaultState() throws Exception {
            when(bookingClient.getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()), ArgumentMatchers.eq(State.ALL),
                    ArgumentMatchers.eq(from), ArgumentMatchers.eq(size))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).getAllByOwnerId(ArgumentMatchers.eq(itemDto.getOwnerId()),
                    ArgumentMatchers.eq(State.ALL), ArgumentMatchers.eq(from), ArgumentMatchers.eq(size));
        }

        @Test
        public void shouldThrowExceptionIfUnknownState() throws Exception {
            mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "unknown", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsNegative() throws Exception {
            size = -1;

            mvc.perform(get("/bookings/owner?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(bookingClient, never()).getAllByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }
}
