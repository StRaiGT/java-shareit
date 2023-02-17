package ru.practicum.shareit.booking.chainSearcher;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingException;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Searcher {
    protected Searcher next;

    public Searcher setNext(Searcher next) {
        this.next = next;
        return next;
    }

    public List<Booking> findAll(Long userId, State state, Pageable pageable,
                                 LocalDateTime dateTime, BookingRepository bookingRepository) {
        if (shouldSearch(state)) {
            return findBooking(userId, pageable, dateTime, bookingRepository);
        } else if (next != null) {
            return next.findAll(userId, state, pageable, dateTime, bookingRepository);
        } else {
            throw new BookingException("State not found.");
        }
    }

    public abstract Boolean shouldSearch(State state);

    public abstract List<Booking> findBooking(Long userId, Pageable pageable,
                                              LocalDateTime dateTime, BookingRepository bookingRepository);
}
