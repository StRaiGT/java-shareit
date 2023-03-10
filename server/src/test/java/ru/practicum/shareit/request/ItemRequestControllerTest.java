package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    private final User user1 = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@yandex.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("Test user 2")
            .email("tester2@yandex.ru")
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(user1.getId())
            .description("item description")
            .created(LocalDateTime.now())
            .build();
    private final ItemDto itemDto1 = ItemDto.builder()
            .id(1L)
            .name("item dto 1")
            .description("item dto 1 description")
            .available(true)
            .ownerId(user1.getId())
            .requestId(1L)
            .build();
    private final ItemDto itemDto2 = ItemDto.builder()
            .id(2L)
            .name("item dto 2")
            .description("item dto 2 description")
            .available(false)
            .ownerId(user1.getId())
            .requestId(2L)
            .build();
    private final ItemRequestExtendedDto itemRequestExtendedDto1 = ItemRequestExtendedDto.builder()
            .id(1L)
            .description("request 1 description")
            .created(LocalDateTime.now())
            .items(List.of(itemDto1, itemDto2))
            .build();
    private final ItemRequestExtendedDto itemRequestExtendedDto2 = ItemRequestExtendedDto.builder()
            .id(2L)
            .description("request 2 description")
            .created(LocalDateTime.now())
            .items(List.of())
            .build();
    private ItemRequestCreateDto itemRequestCreateDto;
    private int from;
    private int size;

    @BeforeEach
    public void beforeEach() {
        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("item description")
                .build();
        from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
    }

    @Nested
    class Create {
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
    }
}