package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private static User user1;
    private static User user2;
    private static ItemRequestDto itemRequestDto;
    private static ItemRequestExtendedDto itemRequestExtendedDto1;
    private static ItemRequestExtendedDto itemRequestExtendedDto2;

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

        itemRequestDto = ItemRequestDto.builder()
                .id(user1.getId())
                .description("item description")
                .created(LocalDateTime.now())
                .build();

        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item dto 1")
                .description("item dto 1 description")
                .available(true)
                .ownerId(user1.getId())
                .requestId(1L)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("item dto 2")
                .description("item dto 2 description")
                .available(false)
                .ownerId(user1.getId())
                .requestId(2L)
                .build();

        itemRequestExtendedDto1 = ItemRequestExtendedDto.builder()
                .id(1L)
                .description("request 1 description")
                .created(LocalDateTime.now())
                .items(List.of(itemDto1, itemDto2))
                .build();

        itemRequestExtendedDto2 = ItemRequestExtendedDto.builder()
                .id(2L)
                .description("request 2 description")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Nested
    class Create {
        private ItemRequestCreateDto itemRequestCreateDto;

        @BeforeEach
        public void beforeEachCreate() {
            itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("item description")
                    .build();
        }

        @Test
        public void shouldCreate() throws Exception {
            when(itemRequestService.create(ArgumentMatchers.eq(user1.getId()), ArgumentMatchers.any(ItemRequestCreateDto.class)))
                    .thenReturn(itemRequestDto);

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user1.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));

            verify(itemRequestService, times(1))
                    .create(ArgumentMatchers.eq(user1.getId()), ArgumentMatchers.any(ItemRequestCreateDto.class));
        }

        @Test
        public void shouldThrowExceptionIfNotDescription() throws Exception {
            itemRequestCreateDto.setDescription(null);

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user1.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemRequestService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsEmpty() throws Exception {
            itemRequestCreateDto.setDescription("");

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user1.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemRequestService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsBlank() throws Exception {
            itemRequestCreateDto.setDescription(" ");

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user1.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemRequestService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() throws Exception {
            when(itemRequestService.getById(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(itemRequestExtendedDto1.getId())))
                    .thenReturn(itemRequestExtendedDto1);

            mvc.perform(get("/requests/{id}", itemRequestExtendedDto1.getId())
                            .header(UserController.headerUserId, user2.getId())
                            .content(mapper.writeValueAsString(itemRequestExtendedDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemRequestExtendedDto1)));

            verify(itemRequestService, times(1))
                    .getById(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(itemRequestExtendedDto1.getId()));
        }
    }

    @Nested
    class GetByRequesterId {
        @Test
        public void shouldGet() throws Exception {
            when(itemRequestService.getByRequesterId(ArgumentMatchers.eq(user2.getId())))
                    .thenReturn(List.of(itemRequestExtendedDto1, itemRequestExtendedDto2));

            mvc.perform(get("/requests")
                            .header(UserController.headerUserId, user2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(
                            List.of(itemRequestExtendedDto1, itemRequestExtendedDto2))));

            verify(itemRequestService, times(1))
                    .getByRequesterId(ArgumentMatchers.eq(user2.getId()));
        }
    }

    @Nested
    class GetAll {
        private int from;
        private int size;

        @BeforeEach
        public void beforeEachGetAll() {
            from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
            size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        }

        @Test
        public void shouldGet() throws Exception {
            when(itemRequestService.getAll(ArgumentMatchers.eq(user1.getId()),
                            ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(itemRequestExtendedDto1, itemRequestExtendedDto2));

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(
                            List.of(itemRequestExtendedDto1, itemRequestExtendedDto2))));

            verify(itemRequestService, times(1)).getAll(ArgumentMatchers.eq(user1.getId()),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldThrowExceptionIfInvalidFrom() throws Exception {
            from = -1;

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemRequestService, never()).getAll(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsNegative() throws Exception {
            size = -1;

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemRequestService, never()).getAll(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemRequestService, never()).getAll(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }
}