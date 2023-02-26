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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.model.CommentRequestDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private ItemClient itemClient;

    private final UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("Test user")
            .email("tester@yandex.ru")
            .build();
    private final ItemDto itemDto1 = ItemDto.builder()
            .id(1L)
            .name("Test item 1")
            .description("Test item description 1")
            .available(true)
            .ownerId(userDto1.getId())
            .requestId(null)
            .build();
    private final String text = "text for search";
    private ItemDto itemDto;
    private CommentRequestDto commentRequestDto;
    private int from;
    private int size;

    @BeforeEach
    public void beforeEach() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item")
                .description("Test item description")
                .available(true)
                .ownerId(userDto1.getId())
                .requestId(null)
                .build();
        commentRequestDto = CommentRequestDto.builder()
                .text("comment 1")
                .build();
        from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() throws Exception {
            when(itemClient.create(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.any(ItemDto.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).create(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.any(ItemDto.class));
        }

        @Test
        public void shouldThrowExceptionIfNameIsNull() throws Exception {
            itemDto.setName(null);

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfNameIsEmpty() throws Exception {
            itemDto.setName("");

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfNameIsBlank() throws Exception {
            itemDto.setName(" ");

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsNull() throws Exception {
            itemDto.setDescription(null);

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsEmpty() throws Exception {
            itemDto.setDescription("");

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfDescriptionIsBlank() throws Exception {
            itemDto.setDescription(" ");

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfAvailableIsNull() throws Exception {
            itemDto.setAvailable(null);

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetByOwner {
        @Test
        public void shouldGet() throws Exception {
            when(itemClient.getByOwnerId(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(from),
                    ArgumentMatchers.eq(size))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).getByOwnerId(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(from), ArgumentMatchers.eq(size));
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemClient, never()).getByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemClient, never()).getByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsNegative() throws Exception {
            size = -1;

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemClient, never()).getByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() throws Exception {
            when(itemClient.getById(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(itemDto1.getId())))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/items/{id}", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).getById(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(itemDto1.getId()));
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldPatch() throws Exception {
            when(itemClient.patch(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(itemDto1.getId()),
                    ArgumentMatchers.any(ItemDto.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(patch("/items/{id}", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).patch(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(itemDto1.getId()), ArgumentMatchers.any(ItemDto.class));
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() throws Exception {
            mvc.perform(delete("/items/{id}", itemDto1.getId()))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).delete(ArgumentMatchers.eq(itemDto1.getId()));
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldSearch() throws Exception {
            when(itemClient.search(ArgumentMatchers.eq(text), ArgumentMatchers.eq(from),
                    ArgumentMatchers.eq(size))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).search(ArgumentMatchers.eq(text),
                    ArgumentMatchers.eq(from), ArgumentMatchers.eq(size));
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isInternalServerError());

            verify(itemClient, never()).search(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isInternalServerError());

            verify(itemClient, never()).search(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsNegative() throws Exception {
            size = -1;

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isInternalServerError());

            verify(itemClient, never()).search(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class AddComment {
        @Test
        public void shouldAdd() throws Exception {
            when(itemClient.addComment(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(itemDto1.getId()),
                    ArgumentMatchers.any(CommentRequestDto.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

            mvc.perform(post("/items/{id}/comment", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(commentRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(itemClient, times(1)).addComment(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(itemDto1.getId()), ArgumentMatchers.any(CommentRequestDto.class));
        }

        @Test
        public void shouldThrowExceptionIfNull() throws Exception {
            commentRequestDto.setText(null);

            mvc.perform(post("/items/{id}/comment", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(commentRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).addComment(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfEmpty() throws Exception {
            commentRequestDto.setText("");

            mvc.perform(post("/items/{id}/comment", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(commentRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).addComment(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfBlank() throws Exception {
            commentRequestDto.setText(" ");

            mvc.perform(post("/items/{id}/comment", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(commentRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(itemClient, never()).addComment(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }
}
