package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestConstrains;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryIT {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private final User user1 = TestConstrains.getUser1();
    private final User user2 = TestConstrains.getUser2();
    private final Item item1 = TestConstrains.getItem1(user1);
    private final Item item2 = TestConstrains.getItem2(user2);
    private final Item item3 = TestConstrains.getItem3(user1);
    private final Pageable pageable = TestConstrains.getPageable();

    @BeforeEach
    void addItems() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Nested
    class FindByOwnerIdOrderByIdAsc {
        @Test
        void shouldGetTwoItems() {
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
        void shouldGetOneItems() {
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
        void shouldGetZeroItems() {
            List<Item> itemsFromRepository = itemRepository.findByOwnerIdOrderByIdAsc(99L, pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(0, itemsFromRepository.size());
        }
    }

    @Nested
    class Search {
        @Test
        void shouldGetTwoAvailableItems() {
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
        void shouldGetZeroItemsIfItemsNotAvailable() {
            List<Item> itemsFromRepository = itemRepository.search("item3", pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(0, itemsFromRepository.size());
        }

        @Test
        void shouldGetZeroItemsIfTextNotFound() {
            List<Item> itemsFromRepository = itemRepository.search("99", pageable)
                    .get()
                    .collect(Collectors.toList());

            assertEquals(0, itemsFromRepository.size());
        }
    }
}
