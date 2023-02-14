package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private static User user1;
    private static User user2;
    private static Item item1;
    private static Item item2;
    private static Item item3;
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
                .name("item1 name")
                .description("seaRch1 description ")
                .available(true)
                .owner(user1)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("item2 name")
                .description("SeARch1 description")
                .available(true)
                .owner(user2)
                .build();

        item3 = Item.builder()
                .id(3L)
                .name("item3 name")
                .description("itEm3 description")
                .available(false)
                .owner(user1)
                .build();

        final int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        final int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        pageable = PageRequest.of(from / size, size);
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Nested
    class FindByOwnerIdOrderByIdAsc {
        @Test
        public void shouldGetTwoItems() {
            List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(user1.getId(), pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(2, itemsFromRepository.size());

            Item itemsFromRepository1 = itemsFromRepository.get(0);
            Item itemsFromRepository2 = itemsFromRepository.get(1);

            assertEquals(item1.getId(), itemsFromRepository1.getId());
            assertEquals(item1.getName(), itemsFromRepository1.getName());
            assertEquals(item1.getDescription(), itemsFromRepository1.getDescription());
            assertEquals(item1.getAvailable(), itemsFromRepository1.getAvailable());
            assertEquals(item1.getOwner().getId(), itemsFromRepository1.getOwner().getId());
            assertEquals(item1.getOwner().getName(), itemsFromRepository1.getOwner().getName());
            assertEquals(item1.getOwner().getEmail(), itemsFromRepository1.getOwner().getEmail());

            assertEquals(item3.getId(), itemsFromRepository2.getId());
            assertEquals(item3.getId(), itemsFromRepository2.getId());
            assertEquals(item3.getName(), itemsFromRepository2.getName());
            assertEquals(item3.getDescription(), itemsFromRepository2.getDescription());
            assertEquals(item3.getAvailable(), itemsFromRepository2.getAvailable());
            assertEquals(item3.getOwner().getId(), itemsFromRepository2.getOwner().getId());
            assertEquals(item3.getOwner().getName(), itemsFromRepository2.getOwner().getName());
            assertEquals(item3.getOwner().getEmail(), itemsFromRepository2.getOwner().getEmail());
        }

        @Test
        public void shouldGetOneItems() {
            List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(user2.getId(), pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(1, itemsFromRepository.size());

            Item itemsFromRepository1 = itemsFromRepository.get(0);

            assertEquals(item2.getId(), itemsFromRepository1.getId());
            assertEquals(item2.getName(), itemsFromRepository1.getName());
            assertEquals(item2.getDescription(), itemsFromRepository1.getDescription());
            assertEquals(item2.getAvailable(), itemsFromRepository1.getAvailable());
            assertEquals(item2.getOwner().getId(), itemsFromRepository1.getOwner().getId());
            assertEquals(item2.getOwner().getName(), itemsFromRepository1.getOwner().getName());
            assertEquals(item2.getOwner().getEmail(), itemsFromRepository1.getOwner().getEmail());
        }

        @Test
        public void shouldGetZeroItems() {
            List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(99L, pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(0, itemsFromRepository.size());
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldGetTwoAvailableItems() {
            List<Item> itemsFromRepository = itemRepository.search("search1", pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(2, itemsFromRepository.size());

            Item itemsFromRepository1 = itemsFromRepository.get(0);
            Item itemsFromRepository2 = itemsFromRepository.get(1);

            assertEquals(item1.getId(), itemsFromRepository1.getId());
            assertEquals(item1.getName(), itemsFromRepository1.getName());
            assertEquals(item1.getDescription(), itemsFromRepository1.getDescription());
            assertEquals(item1.getAvailable(), itemsFromRepository1.getAvailable());
            assertEquals(item1.getOwner().getId(), itemsFromRepository1.getOwner().getId());
            assertEquals(item1.getOwner().getName(), itemsFromRepository1.getOwner().getName());
            assertEquals(item1.getOwner().getEmail(), itemsFromRepository1.getOwner().getEmail());

            assertEquals(item2.getId(), itemsFromRepository2.getId());
            assertEquals(item2.getId(), itemsFromRepository2.getId());
            assertEquals(item2.getName(), itemsFromRepository2.getName());
            assertEquals(item2.getDescription(), itemsFromRepository2.getDescription());
            assertEquals(item2.getAvailable(), itemsFromRepository2.getAvailable());
            assertEquals(item2.getOwner().getId(), itemsFromRepository2.getOwner().getId());
            assertEquals(item2.getOwner().getName(), itemsFromRepository2.getOwner().getName());
            assertEquals(item2.getOwner().getEmail(), itemsFromRepository2.getOwner().getEmail());
        }

        @Test
        public void shouldGetZeroItemsIfItemsNotAvailable() {
            List<Item> itemsFromRepository = itemRepository.search("item3", pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(0, itemsFromRepository.size());
        }

        @Test
        public void shouldGetZeroItemsIfTextNotFound() {
            List<Item> itemsFromRepository = itemRepository.search("99", pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(0, itemsFromRepository.size());
        }
    }
}
