package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    private final UserController userController;
    private final ItemController itemController;

    @Nested
    class CreateItem {
        @Test
        public void shouldCreate() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto.getOwnerId(), itemDto);

            List<ItemDto> itemsFromController = itemController.getItemsByOwner(userDto.getId());

            assertEquals(itemsFromController.size(), 1);

            ItemDto itemDtoFromController = itemsFromController.get(0);

            assertEquals(itemDtoFromController.getId(), itemDto.getId());
            assertEquals(itemDtoFromController.getName(), itemDto.getName());
            assertEquals(itemDtoFromController.getDescription(), itemDto.getDescription());
            assertEquals(itemDtoFromController.getAvailable(), itemDto.getAvailable());
            assertEquals(itemDtoFromController.getOwnerId(), itemDto.getOwnerId());
            assertEquals(itemDtoFromController.getRequest(), itemDto.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemOwnerIdNotFound() {
            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(10L)
                    .request(null)
                    .build();
            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.createItem(10L, itemDto));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        }
    }

    @Nested
    class GetItemsByOwner {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.createUser(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2")
                    .description("Test item description 2")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto2.getOwnerId(), itemDto2);

            ItemDto itemDto3 = ItemDto.builder()
                    .id(3L)
                    .name("Test item 3")
                    .description("Test item description 3")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto3.getOwnerId(), itemDto3);

            List<ItemDto> itemsFromController1 = itemController.getItemsByOwner(userDto1.getId());

            assertEquals(itemsFromController1.size(), 2);

            ItemDto itemDtoFromController1 = itemsFromController1.get(0);
            ItemDto itemDtoFromController3 = itemsFromController1.get(1);

            assertEquals(itemDtoFromController1.getId(), itemDto1.getId());
            assertEquals(itemDtoFromController1.getName(), itemDto1.getName());
            assertEquals(itemDtoFromController1.getDescription(), itemDto1.getDescription());
            assertEquals(itemDtoFromController1.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemDtoFromController1.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemDtoFromController1.getRequest(), itemDto1.getRequest());

            assertEquals(itemDtoFromController3.getId(), itemDto3.getId());
            assertEquals(itemDtoFromController3.getName(), itemDto3.getName());
            assertEquals(itemDtoFromController3.getDescription(), itemDto3.getDescription());
            assertEquals(itemDtoFromController3.getAvailable(), itemDto3.getAvailable());
            assertEquals(itemDtoFromController3.getOwnerId(), itemDto3.getOwnerId());
            assertEquals(itemDtoFromController3.getRequest(), itemDto3.getRequest());

            List<ItemDto> itemsFromController2 = itemController.getItemsByOwner(userDto2.getId());

            assertEquals(itemsFromController2.size(), 1);

            ItemDto itemDtoFromController2 = itemsFromController2.get(0);

            assertEquals(itemDtoFromController2.getId(), itemDto2.getId());
            assertEquals(itemDtoFromController2.getName(), itemDto2.getName());
            assertEquals(itemDtoFromController2.getDescription(), itemDto2.getDescription());
            assertEquals(itemDtoFromController2.getAvailable(), itemDto2.getAvailable());
            assertEquals(itemDtoFromController2.getOwnerId(), itemDto2.getOwnerId());
            assertEquals(itemDtoFromController2.getRequest(), itemDto2.getRequest());
        }

        @Test
        public void shouldGetIfEmpty() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            List<ItemDto> itemsFromController = itemController.getItemsByOwner(userDto.getId());

            assertEquals(itemsFromController.size(), 0);
        }
    }

    @Nested
    class GetItemById {
        @Test
        public void shouldGet() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto.getOwnerId(), itemDto);

            ItemDto itemFromController = itemController.getItemById(itemDto.getId());

            assertEquals(itemFromController.getId(), itemDto.getId());
            assertEquals(itemFromController.getName(), itemDto.getName());
            assertEquals(itemFromController.getDescription(), itemDto.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemIdNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.getItemById(10L));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
        }
    }

    @Nested
    class PatchItem {
        @Test
        public void shouldPatch() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Patch test item 1")
                    .description("Patch test item description 1")
                    .available(false)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.patchItem(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2);

            ItemDto itemFromController = itemController.getItemById(itemDto1.getId());

            assertEquals(itemFromController.getId(), itemDto1.getId());
            assertEquals(itemFromController.getName(), itemDto2.getName());
            assertEquals(itemFromController.getDescription(), itemDto2.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto2.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto2.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto2.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemOwnerIdNotFound() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Patch test item 1")
                    .description("Patch test item description 1")
                    .available(false)
                    .ownerId(10L)
                    .request(null)
                    .build();

            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.patchItem(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());

            ItemDto itemFromController = itemController.getItemById(itemDto1.getId());

            assertEquals(itemFromController.getId(), itemDto1.getId());
            assertEquals(itemFromController.getName(), itemDto1.getName());
            assertEquals(itemFromController.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto1.getRequest());
        }

        @Test
        public void shouldThrowExceptionIfItemOwnerIdForbidden() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.createUser(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Patch test item 1")
                    .description("Patch test item description 1")
                    .available(false)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();

            ForbiddenException exception = assertThrows(ForbiddenException.class, () -> itemController.patchItem(itemDto2.getOwnerId(), itemDto1.getId(), itemDto2));
            assertEquals("Изменение вещи доступно только владельцу.", exception.getMessage());

            ItemDto itemFromController = itemController.getItemById(itemDto1.getId());

            assertEquals(itemFromController.getId(), itemDto1.getId());
            assertEquals(itemFromController.getName(), itemDto1.getName());
            assertEquals(itemFromController.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController.getRequest(), itemDto1.getRequest());
        }
    }

    @Nested
    class DeleteItem {
        @Test
        public void shouldDelete() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.createItem(userDto.getId(), itemDto);

            itemController.deleteItem(itemDto.getId());

            assertEquals(itemController.getItemsByOwner(userDto.getId()).size(), 0);
        }

        @Test
        public void shouldDeleteIfItemIdNotFound() {
            itemController.deleteItem(10L);

            NotFoundException exception = assertThrows(NotFoundException.class, () -> itemController.getItemById(10L));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldSearch() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.createUser(userDto2);

            ItemDto itemDto1 = ItemDto.builder()
                    .id(1L)
                    .name("Test item 1 SeCREt")
                    .description("Test item description 1")
                    .available(true)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto1.getOwnerId(), itemDto1);

            ItemDto itemDto2 = ItemDto.builder()
                    .id(2L)
                    .name("Test item 2 SeCREt")
                    .description("Test item description 2 SeCREt")
                    .available(false)
                    .ownerId(userDto1.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto2.getOwnerId(), itemDto2);

            ItemDto itemDto3 = ItemDto.builder()
                    .id(3L)
                    .name("Test item 3")
                    .description("Test item description 3 SeCREt")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto3.getOwnerId(), itemDto3);

            ItemDto itemDto4 = ItemDto.builder()
                    .id(4L)
                    .name("Test item 4")
                    .description("Test item description 4")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto4.getOwnerId(), itemDto4);

            List<ItemDto> itemsFromController = itemController.search("sEcrEt");

            assertEquals(itemsFromController.size(), 2);

            ItemDto itemFromController1 = itemsFromController.get(0);
            ItemDto itemFromController2 = itemsFromController.get(1);

            assertEquals(itemFromController1.getId(), itemDto1.getId());
            assertEquals(itemFromController1.getName(), itemDto1.getName());
            assertEquals(itemFromController1.getDescription(), itemDto1.getDescription());
            assertEquals(itemFromController1.getAvailable(), itemDto1.getAvailable());
            assertEquals(itemFromController1.getOwnerId(), itemDto1.getOwnerId());
            assertEquals(itemFromController1.getRequest(), itemDto1.getRequest());

            assertEquals(itemFromController2.getId(), itemDto3.getId());
            assertEquals(itemFromController2.getName(), itemDto3.getName());
            assertEquals(itemFromController2.getDescription(), itemDto3.getDescription());
            assertEquals(itemFromController2.getAvailable(), itemDto3.getAvailable());
            assertEquals(itemFromController2.getOwnerId(), itemDto3.getOwnerId());
            assertEquals(itemFromController2.getRequest(), itemDto3.getRequest());
        }

        @Test
        public void shouldSearchIfEmpty() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto.getId())
                    .request(null)
                    .build();
            itemController.createItem(itemDto.getOwnerId(), itemDto);

            List<ItemDto> itemsFromController = itemController.search(" ");

            assertEquals(itemsFromController.size(), 0);
        }
    }
}
