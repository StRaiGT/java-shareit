package ru.practicum.shareit.request;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestConstrains;
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

    private final User user = TestConstrains.getUser1();
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final List<Item> items = List.of(TestConstrains.getItem1WithRequest(user));
    private final List<ItemDto> itemsDto = List.of(TestConstrains.getItem1WithCommentsDto(user));
    private final ItemRequest itemRequest = TestConstrains.getItemRequest1(user, dateTime, items);
    private final ItemRequestCreateDto itemRequestCreateDto = TestConstrains.getItem1RequestCreateDto();

    @Nested
    class ToItemRequest {
        @Test
        void shouldReturnItemRequest() {
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
        void shouldReturnNull() {
            ItemRequest result = itemRequestMapper.toItemRequest(null, null, null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemRequestDto {
        @Test
        void shouldReturnItemRequestDto() {
            ItemRequestDto result = itemRequestMapper.toItemRequestDto(itemRequest);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
        }

        @Test
        void shouldReturnNull() {
            ItemRequestDto result = itemRequestMapper.toItemRequestDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemRequestExtendedDto {
        @Test
        void shouldReturnItemRequestExtendedDto() {
            ItemRequestExtendedDto result = itemRequestMapper.toItemRequestExtendedDto(itemRequest, itemsDto);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
            assertEquals(itemsDto, result.getItems());
        }

        @Test
        void shouldReturnNull() {
            ItemRequestExtendedDto result = itemRequestMapper.toItemRequestExtendedDto(null, null);

            assertNull(result);
        }
    }
}
