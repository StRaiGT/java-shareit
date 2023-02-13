package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestFullContextTest {
    private final UserController userController;
    private final ItemController itemController;
    private final ItemRequestController itemRequestController;

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("description")
                    .build();

            ItemRequestDto itemRequestDto = itemRequestController.create(userDto1.getId(), itemRequestCreateDto);

            assertEquals(1L, itemRequestDto.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestDto.getDescription());
            assertNotNull(itemRequestDto.getCreated());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGetWithItems() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("description")
                    .build();
            ItemRequestDto itemRequestDto = itemRequestController.create(userDto1.getId(), itemRequestCreateDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(itemRequestDto.getId())
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            ItemRequestExtendedDto itemRequestFromController = itemRequestController.getById(userDto1.getId(), itemRequestDto.getId());

            assertEquals(1L, itemRequestFromController.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestFromController.getDescription());
            assertNotNull(itemRequestFromController.getCreated());

            assertNotNull(itemRequestFromController.getItems());
            assertEquals(1, itemRequestFromController.getItems().size());
            assertEquals(itemDto.getId(), itemRequestFromController.getItems().get(0).getId());
            assertEquals(itemDto.getDescription(), itemRequestFromController.getItems().get(0).getDescription());
            assertEquals(itemDto.getAvailable(), itemRequestFromController.getItems().get(0).getAvailable());
            assertEquals(itemDto.getOwnerId(), itemRequestFromController.getItems().get(0).getOwnerId());
            assertEquals(itemDto.getRequestId(), itemRequestFromController.getItems().get(0).getRequestId());
        }
    }

    @Nested
    class GetByRequesterId {
        @Test
        public void shouldGetWithItems() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("description")
                    .build();
            ItemRequestDto itemRequestDto = itemRequestController.create(userDto1.getId(), itemRequestCreateDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(itemRequestDto.getId())
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            List<ItemRequestExtendedDto> itemRequestsFromController = itemRequestController.getByRequesterId(userDto1.getId());

            assertEquals(1, itemRequestsFromController.size());

            ItemRequestExtendedDto itemRequestFromController = itemRequestsFromController.get(0);

            assertEquals(1L, itemRequestFromController.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestFromController.getDescription());
            assertNotNull(itemRequestFromController.getCreated());

            assertNotNull(itemRequestFromController.getItems());
            assertEquals(1, itemRequestFromController.getItems().size());
            assertEquals(itemDto.getId(), itemRequestFromController.getItems().get(0).getId());
            assertEquals(itemDto.getDescription(), itemRequestFromController.getItems().get(0).getDescription());
            assertEquals(itemDto.getAvailable(), itemRequestFromController.getItems().get(0).getAvailable());
            assertEquals(itemDto.getOwnerId(), itemRequestFromController.getItems().get(0).getOwnerId());
            assertEquals(itemDto.getRequestId(), itemRequestFromController.getItems().get(0).getRequestId());
        }
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGetAllWhereNotOwner() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                    .description("description")
                    .build();
            ItemRequestDto itemRequestDto = itemRequestController.create(userDto1.getId(), itemRequestCreateDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(itemRequestDto.getId())
                    .build();
            itemController.create(itemDto.getOwnerId(), itemDto);

            List<ItemRequestExtendedDto> itemRequestsFromController = itemRequestController.getAll(
                    userDto2.getId(),
                    Integer.parseInt(UserController.PAGE_DEFAULT_FROM),
                    Integer.parseInt(UserController.PAGE_DEFAULT_SIZE));

            assertEquals(1, itemRequestsFromController.size());

            ItemRequestExtendedDto itemRequestFromController = itemRequestsFromController.get(0);

            assertEquals(1L, itemRequestFromController.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestFromController.getDescription());
            assertNotNull(itemRequestFromController.getCreated());

            assertNotNull(itemRequestFromController.getItems());
            assertEquals(1, itemRequestFromController.getItems().size());
            assertEquals(itemDto.getId(), itemRequestFromController.getItems().get(0).getId());
            assertEquals(itemDto.getDescription(), itemRequestFromController.getItems().get(0).getDescription());
            assertEquals(itemDto.getAvailable(), itemRequestFromController.getItems().get(0).getAvailable());
            assertEquals(itemDto.getOwnerId(), itemRequestFromController.getItems().get(0).getOwnerId());
            assertEquals(itemDto.getRequestId(), itemRequestFromController.getItems().get(0).getRequestId());
        }
    }
}
