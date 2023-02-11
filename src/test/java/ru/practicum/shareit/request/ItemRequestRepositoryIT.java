package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryIT {
    private final User user1 = TestConstrains.getUser1();
    private final User user2 = TestConstrains.getUser2();
    private final Item item1 = TestConstrains.getItem1WithRequest(user1);
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final ItemRequest itemRequest1 = TestConstrains.getItemRequest1(user2, dateTime, null);

    private final Pageable pageable = TestConstrains.getPageable();

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void addRequests() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRepository.save(item1);
    }

    @Nested
    class FindByRequesterIdIdOrderByCreatedAsc {
        @Test
        void shouldGetOne() {
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
        void shouldGetZeroIfNotRequests() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user1.getId());

            assertEquals(0, itemsRequest.size());
        }
    }

    @Nested
    class FindByRequesterIdIdNot {
        @Test
        void shouldGetZeroIfOwner() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequesterId_IdNot(user2.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(0, itemsRequest.size());
        }

        @Test
        void shouldGetOneIfNotOwner() {
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
