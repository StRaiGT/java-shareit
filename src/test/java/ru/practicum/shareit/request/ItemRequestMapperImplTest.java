package ru.practicum.shareit.request;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperImplTest {
    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;

    private final LocalDateTime dateTime = LocalDateTime.of(2023,1,1,10,0,0);
    private final User user = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@yandex.ru")
            .build();
    private final List<Item> items = List.of(Item.builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .owner(user)
            .requestId(1L)
            .build());
    private final List<ItemDto> itemsDto = List.of(ItemDto.builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .ownerId(user.getId())
            .requestId(1L)
            .build());
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("itemRequest1 description")
            .requesterId(user)
            .created(dateTime)
            .items(items)
            .build();
    private final ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
            .description("item description")
            .build();

    @Nested
    class ToItemRequest {
        @Test
        public void shouldReturnItemRequest() {
            ItemRequest result = itemRequestMapper.toItemRequest(itemRequestCreateDto, user, dateTime);

            assertNull(result.getId());
            assertEquals(itemRequestCreateDto.getDescription(), result.getDescription());
            assertEquals(user.getId(), result.getRequesterId().getId());
            assertEquals(user.getName(), result.getRequesterId().getName());
            assertEquals(user.getEmail(), result.getRequesterId().getEmail());
            assertEquals(dateTime, result.getCreated());
            assertNull(result.getItems());
        }

        @Test
        public void shouldReturnNull() {
            ItemRequest result = itemRequestMapper.toItemRequest(null, null, null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemRequestDto {
        @Test
        public void shouldReturnItemRequestDto() {
            ItemRequestDto result = itemRequestMapper.toItemRequestDto(itemRequest);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
        }

        @Test
        public void shouldReturnNull() {
            ItemRequestDto result = itemRequestMapper.toItemRequestDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemRequestExtendedDto {
        @Test
        public void shouldReturnItemRequestExtendedDto() {
            ItemRequestExtendedDto result = itemRequestMapper.toItemRequestExtendedDto(itemRequest, itemsDto);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
            assertEquals(itemsDto, result.getItems());
        }

        @Test
        public void shouldReturnNull() {
            ItemRequestExtendedDto result = itemRequestMapper.toItemRequestExtendedDto(null, null);

            assertNull(result);
        }
    }
}
