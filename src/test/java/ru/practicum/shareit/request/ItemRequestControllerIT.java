package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
public class ItemRequestControllerIT {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @BeforeAll
    static void addData() {
        //TODO
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() throws Exception {
            User user = User.builder()
                    .id(1L)
                    .build();

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("item description")
                    .build();

            ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                    .id(user.getId())
                    .description("item description")
                    .created(LocalDateTime.now())
                    .build();

            when(itemRequestService.create(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any(ItemRequestCreateDto.class)))
                    .thenReturn(itemRequestDto);

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));

            verify(itemRequestService, times(1))
                    .create(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any(ItemRequestCreateDto.class));
        }

        @Test
        public void shouldThrowExceptionIfNotDescription() throws Exception {
            User user = User.builder()
                    .id(1L)
                    .build();

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .build();

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemRequestService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsEmpty() throws Exception {
            User user = User.builder()
                    .id(1L)
                    .build();

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("")
                    .build();

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user.getId())
                            .content(mapper.writeValueAsString(itemRequestCreateDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemRequestService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsBlank() throws Exception {
            User user = User.builder()
                    .id(1L)
                    .build();

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description(" ")
                    .build();

            mvc.perform(post("/requests")
                            .header(UserController.headerUserId, user.getId())
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
            User user1 = User.builder()
                    .id(1L)
                    .build();

            User user2 = User.builder()
                    .id(2L)
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

            ItemRequestExtendedDto itemRequestExtendedDto = ItemRequestExtendedDto.builder()
                    .id(1L)
                    .description("request description")
                    .created(LocalDateTime.now())
                    .items(List.of(itemDto1, itemDto2))
                    .build();

            when(itemRequestService.getById(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(itemRequestExtendedDto.getId())))
                    .thenReturn(itemRequestExtendedDto);

            mvc.perform(get("/requests/{id}", itemRequestExtendedDto.getId())
                            .header(UserController.headerUserId, user2.getId())
                            .content(mapper.writeValueAsString(itemRequestExtendedDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemRequestExtendedDto)));

            verify(itemRequestService, times(1))
                    .getById(ArgumentMatchers.eq(user2.getId()), ArgumentMatchers.eq(itemRequestExtendedDto.getId()));
        }
    }

    @Nested
    class GetByRequesterId {
        @Test
        public void shouldGet() throws Exception {
            User user1 = User.builder()
                    .id(1L)
                    .build();

            User user2 = User.builder()
                    .id(2L)
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

            ItemRequestExtendedDto itemRequestExtendedDto1 = ItemRequestExtendedDto.builder()
                    .id(1L)
                    .description("request 1 description")
                    .created(LocalDateTime.now())
                    .items(List.of(itemDto1, itemDto2))
                    .build();

            ItemRequestExtendedDto itemRequestExtendedDto2 = ItemRequestExtendedDto.builder()
                    .id(2L)
                    .description("request 2 description")
                    .created(LocalDateTime.now())
                    .items(List.of())
                    .build();

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
        @Test
        public void shouldGet() throws Exception {
            User user1 = User.builder()
                    .id(1L)
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

            ItemRequestExtendedDto itemRequestExtendedDto1 = ItemRequestExtendedDto.builder()
                    .id(1L)
                    .description("request 1 description")
                    .created(LocalDateTime.now())
                    .items(List.of(itemDto1, itemDto2))
                    .build();

            ItemRequestExtendedDto itemRequestExtendedDto2 = ItemRequestExtendedDto.builder()
                    .id(2L)
                    .description("request 2 description")
                    .created(LocalDateTime.now())
                    .items(List.of())
                    .build();

            int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
            int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);

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
            User user1 = User.builder()
                    .id(1L)
                    .build();

            int from = -1;
            int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemRequestService, never()).getAll(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsNegative() throws Exception {
            User user1 = User.builder()
                    .id(1L)
                    .build();

            int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
            int size = -1;

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemRequestService, never()).getAll(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            User user1 = User.builder()
                    .id(1L)
                    .build();

            int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
            int size = 0;

            mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, user1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemRequestService, never()).getAll(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }
}
