package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestConstrains;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryIT {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private final User user1 = TestConstrains.getUser1();
    private final User user2 = TestConstrains.getUser2();
    private final Item item1 = TestConstrains.getItem1(user1);
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final Booking bookingPast = TestConstrains.getBooking1(user2, item1, dateTime);
    private final Booking bookingCurrent = TestConstrains.getBooking2(user2, item1, dateTime);
    private final Booking bookingFuture = TestConstrains.getBooking3(user2, item1, dateTime);
    private final Booking bookingRejected = TestConstrains.getBooking4(user2, item1, dateTime);
    private final Pageable pageable = TestConstrains.getPageable();

    @BeforeEach
    void addBookings() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        bookingRepository.save(bookingPast);
        bookingRepository.save(bookingCurrent);
        bookingRepository.save(bookingFuture);
        bookingRepository.save(bookingRejected);
    }

    @Nested
    class FindByBookerIdOrderByStartDesc {
        @Test
        void shouldGetAll() {
            List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(user2.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(4, result.size());
            assertEquals(bookingRejected.getId(), result.get(0).getId());
            assertEquals(bookingFuture.getId(), result.get(1).getId());
            assertEquals(bookingCurrent.getId(), result.get(2).getId());
            assertEquals(bookingPast.getId(), result.get(3).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(user1.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc {
        @Test
        void shouldGetCurrent() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user2.getId(), dateTime,
                            dateTime, pageable).get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingCurrent.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user1.getId(), dateTime,
                            dateTime, pageable).get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc {
        @Test
        void shouldGetPast() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(user2.getId(), dateTime,
                            Status.APPROVED, pageable).get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingPast.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(user1.getId(), dateTime,
                            Status.APPROVED, pageable).get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByBookerIdAndStartAfterOrderByStartDesc {
        @Test
        void shouldGetFuture() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStartAfterOrderByStartDesc(user2.getId(), dateTime, pageable)
                    .get().collect(Collectors.toList());

            assertEquals(2, result.size());
            assertEquals(bookingRejected.getId(), result.get(0).getId());
            assertEquals(bookingFuture.getId(), result.get(1).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStartAfterOrderByStartDesc(user1.getId(), dateTime, pageable)
                    .get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByBookerIdAndStatusEqualsOrderByStartDesc {
        @Test
        void shouldGetWaiting() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(user2.getId(), Status.WAITING, pageable)
                    .get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingFuture.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetRejected() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(user2.getId(), Status.REJECTED, pageable)
                    .get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingRejected.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(user1.getId(), Status.WAITING, pageable)
                    .get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemOwnerIdOrderByStartDesc {
        @Test
        void shouldGetAll() {
            List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(user1.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(4, result.size());
            assertEquals(bookingRejected.getId(), result.get(0).getId());
            assertEquals(bookingFuture.getId(), result.get(1).getId());
            assertEquals(bookingCurrent.getId(), result.get(2).getId());
            assertEquals(bookingPast.getId(), result.get(3).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(user2.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc {
        @Test
        void shouldGetCurrent() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    user1.getId(), dateTime, dateTime, pageable).get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingCurrent.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    user2.getId(), dateTime, dateTime, pageable).get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc {
        @Test
        void shouldGetPast() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                    user1.getId(), dateTime, Status.APPROVED, pageable).get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingPast.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                    user2.getId(), dateTime, Status.APPROVED, pageable).get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemOwnerIdAndStartAfterOrderByStartDesc {
        @Test
        void shouldGetFuture() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(user1.getId(),
                    dateTime, pageable).get().collect(Collectors.toList());

            assertEquals(2, result.size());
            assertEquals(bookingRejected.getId(), result.get(0).getId());
            assertEquals(bookingFuture.getId(), result.get(1).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(user2.getId(),
                    dateTime, pageable).get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemOwnerIdAndStatusEqualsOrderByStartDesc {
        @Test
        void shouldGetWaiting() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(user1.getId(),
                    Status.WAITING, pageable).get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingFuture.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetRejected() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(user1.getId(),
                    Status.REJECTED, pageable).get().collect(Collectors.toList());

            assertEquals(1, result.size());
            assertEquals(bookingRejected.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(user2.getId(),
                    Status.WAITING, pageable).get().collect(Collectors.toList());

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc {
        @Test
        void shouldGetLastBookings() {
            List<Booking> result = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                    item1.getId(), dateTime, Status.APPROVED);

            assertEquals(2, result.size());
            assertEquals(bookingCurrent.getId(), result.get(0).getId());
            assertEquals(bookingPast.getId(), result.get(1).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(
                    item1.getId(), dateTime.minusYears(15), Status.APPROVED);

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc {
        @Test
        void shouldGetNextBookings() {
            List<Booking> result = bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(
                    item1.getId(), dateTime, Status.WAITING);

            assertEquals(1, result.size());
            assertEquals(bookingFuture.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(
                    item1.getId(), dateTime, Status.APPROVED);

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals {
        @Test
        void shouldGetFinishedBookings() {
            List<Booking> result = bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(
                    item1.getId(), user2.getId(), dateTime, Status.APPROVED);

            assertEquals(1, result.size());
            assertEquals(bookingPast.getId(), result.get(0).getId());
        }

        @Test
        void shouldGetEmpty() {
            List<Booking> result = bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(
                    item1.getId(), user2.getId(), dateTime.minusYears(15), Status.APPROVED);

            assertEquals(0, result.size());
        }
    }
}
