package ru.practicum.shareit.booking.chainSearcher;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Searcher {
    protected Searcher next;

    public Searcher setNext(Searcher next) {
        this.next = next;
        return next;
    }

    public abstract List<Booking> findAll(Long userId, State state, Pageable pageable,
                                             LocalDateTime dateTime, BookingRepository bookingRepository);
}
