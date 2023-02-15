package ru.practicum.shareit.booking.chainSearcher.owner;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.chainSearcher.Searcher;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SearcherByOwnerIdAndStateRejected extends Searcher {
    @Override
    public List<Booking> findAll(Long userId, State state, Pageable pageable,
                                    LocalDateTime dateTime, BookingRepository bookingRepository) {
        if (state.equals(State.REJECTED)) {
            return bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                    userId, Status.REJECTED, pageable).toList();
        } else if (next != null) {
            return next.findAll(userId, state, pageable, dateTime, bookingRepository);
        } else {
            return new ArrayList<>();
        }
    }
}
