package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private static User user1;
    private static User user2;
    private static Item item1;
    private static LocalDateTime dateTime;
    private static ItemRequest itemRequest1;
    private static Pageable pageable;

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

        item1 = Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .owner(user1)
                .requestId(1L)
                .build();

        dateTime = LocalDateTime.of(2023,1,1,10,0,0);

        final int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        final int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        pageable = PageRequest.of(from / size, size);

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("itemRequest1 description")
                .requesterId(user2)
                .created(dateTime)
                .items(null)
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRepository.save(item1);
    }

    @Nested
    class FindByRequesterIdIdOrderByCreatedAsc {
        @Test
        public void shouldGetOne() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user2.getId());

            assertEquals(1, itemsRequest.size());
            assertEquals(itemRequest1.getId(), itemsRequest.get(0).getId());
            assertEquals(itemRequest1.getDescription(), itemsRequest.get(0).getDescription());
            assertEquals(user2.getId(), itemsRequest.get(0).getRequesterId().getId());
            assertEquals(user2.getName(), itemsRequest.get(0).getRequesterId().getName());
            assertEquals(user2.getEmail(), itemsRequest.get(0).getRequesterId().getEmail());
            assertEquals(dateTime, itemsRequest.get(0).getCreated());
        }

        @Test
        public void shouldGetZeroIfNotRequests() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user1.getId());

            assertEquals(0, itemsRequest.size());
        }
    }

    @Nested
    class FindByRequesterIdIdNot {
        @Test
        public void shouldGetZeroIfOwner() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdNot(user2.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(0, itemsRequest.size());
        }

        @Test
        public void shouldGetOneIfNotOwner() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdNot(user1.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(1, itemsRequest.size());
            assertEquals(itemRequest1.getId(), itemsRequest.get(0).getId());
            assertEquals(itemRequest1.getDescription(), itemsRequest.get(0).getDescription());
            assertEquals(user2.getId(), itemsRequest.get(0).getRequesterId().getId());
            assertEquals(user2.getName(), itemsRequest.get(0).getRequesterId().getName());
            assertEquals(user2.getEmail(), itemsRequest.get(0).getRequesterId().getEmail());
            assertEquals(dateTime, itemsRequest.get(0).getCreated());
        }
    }
}
