package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.item.ItemController;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    /*
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("Test user")
            .email("tester@yandex.ru")
            .build();
    private final UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("Test user 2")
            .email("tester2@yandex.ru")
            .build();
    private final ItemDto itemDto1 = ItemDto.builder()
            .id(1L)
            .name("Test item 1")
            .description("Test item description 1")
            .available(true)
            .ownerId(userDto1.getId())
            .requestId(null)
            .build();
    private final ItemDto itemDto2 = ItemDto.builder()
            .id(2L)
            .name("Test item 2")
            .description("Test item description 2")
            .available(true)
            .ownerId(userDto2.getId())
            .requestId(null)
            .build();
    private final BookingItemDto bookingItemDto1 = BookingItemDto.builder()
            .id(1L)
            .bookerId(userDto2.getId())
            .start(LocalDateTime.now().minusMinutes(10))
            .end(LocalDateTime.now().minusMinutes(5))
            .build();
    private final BookingItemDto bookingItemDto2 = BookingItemDto.builder()
            .id(2L)
            .bookerId(userDto2.getId())
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusMinutes(10))
            .build();
    private final CommentDto commentDto1 = CommentDto.builder()
            .id(1L)
            .text("comment 1")
            .createdDate(LocalDateTime.now().minusMinutes(10))
            .authorName(userDto2.getName())
            .build();
    private final CommentDto commentDto2 = CommentDto.builder()
            .id(2L)
            .text("comment 2")
            .createdDate(LocalDateTime.now().minusMinutes(5))
            .authorName(userDto2.getName())
            .build();
    private final ItemExtendedDto itemExtendedDto1 = ItemExtendedDto.builder()
            .id(itemDto1.getId())
            .name(itemDto1.getName())
            .description(itemDto1.getDescription())
            .available(itemDto1.getAvailable())
            .ownerId(itemDto1.getOwnerId())
            .requestId(null)
            .lastBooking(bookingItemDto1)
            .nextBooking(bookingItemDto2)
            .comments(List.of(commentDto1, commentDto2))
            .build();
    private final ItemExtendedDto itemExtendedDto2 = ItemExtendedDto.builder()
            .id(itemDto2.getId())
            .name(itemDto2.getName())
            .description(itemDto2.getDescription())
            .available(itemDto2.getAvailable())
            .ownerId(itemDto2.getOwnerId())
            .requestId(null)
            .lastBooking(null)
            .nextBooking(null)
            .comments(List.of())
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
            when(itemService.create(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.any(ItemDto.class)))
                    .thenReturn(itemDto);

            mvc.perform(post("/items")
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemDto)));

            verify(itemService, times(1)).create(ArgumentMatchers.eq(userDto1.getId()),
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
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

            verify(itemService, never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetByOwner {
        @Test
        public void shouldGet() throws Exception {
            when(itemService.getByOwnerId(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(itemExtendedDto1, itemExtendedDto2));

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(itemExtendedDto1, itemExtendedDto2))));

            verify(itemService, times(1)).getByOwnerId(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemService, never()).getByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/items?from={from}&size={size}", from, size)
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isInternalServerError());

            verify(itemService, never()).getByOwnerId(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() throws Exception {
            when(itemService.getById(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(itemDto1.getId())))
                    .thenReturn(itemExtendedDto1);

            mvc.perform(get("/items/{id}", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemExtendedDto1)));

            verify(itemService, times(1)).getById(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(itemDto1.getId()));
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldPatch() throws Exception {
            when(itemService.patch(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(itemDto1.getId()),
                    ArgumentMatchers.any(ItemDto.class)))
                    .thenReturn(itemDto1);

            mvc.perform(patch("/items/{id}", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(itemDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemDto1)));

            verify(itemService, times(1)).patch(ArgumentMatchers.eq(userDto1.getId()),
                    ArgumentMatchers.eq(itemDto1.getId()), ArgumentMatchers.any(ItemDto.class));
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() throws Exception {
            mvc.perform(delete("/items/{id}", itemDto1.getId()))
                    .andExpect(status().isOk());

            verify(itemService, times(1)).delete(ArgumentMatchers.eq(itemDto1.getId()));
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldSearch() throws Exception {
            when(itemService.search(ArgumentMatchers.eq(text),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size))))
                    .thenReturn(List.of(itemDto1, itemDto2));

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto1, itemDto2))));

            verify(itemService, times(1)).search(ArgumentMatchers.eq(text),
                    ArgumentMatchers.eq(PageRequest.of(from / size, size)));
        }

        @Test
        public void shouldThrowExceptionIfFromIsNegative() throws Exception {
            from = -1;

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isInternalServerError());

            verify(itemService, never()).search(ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        public void shouldThrowExceptionIfSizeIsZero() throws Exception {
            size = 0;

            mvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
                    .andExpect(status().isInternalServerError());

            verify(itemService, never()).search(ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Nested
    class AddComment {
        @Test
        public void shouldAdd() throws Exception {
            when(itemService.addComment(ArgumentMatchers.eq(userDto1.getId()), ArgumentMatchers.eq(itemDto1.getId()),
                    ArgumentMatchers.any(CommentRequestDto.class)))
                    .thenReturn(commentDto1);

            mvc.perform(post("/items/{id}/comment", itemDto1.getId())
                            .header(UserController.headerUserId, userDto1.getId())
                            .content(mapper.writeValueAsString(commentRequestDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(commentDto1)));

            verify(itemService, times(1)).addComment(ArgumentMatchers.eq(userDto1.getId()),
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

            verify(itemService, never()).addComment(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any());
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

            verify(itemService, never()).addComment(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any());
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

            verify(itemService, never()).addComment(ArgumentMatchers.any(), ArgumentMatchers.any(),
                    ArgumentMatchers.any());
        }
    }
*/
}
