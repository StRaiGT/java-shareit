package ru.practicum.shareit.booking.chainSearcher.booker;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.chainSearcher.Searcher;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

public class SearcherByBookerIdAndStatePast extends Searcher {
    @Override
    public Boolean shouldSearch(State state) {
        return state.equals(State.PAST);
    }

    @Override
    public List<Booking> findBooking(Long userId, Pageable pageable,
                                     LocalDateTime dateTime, BookingRepository bookingRepository) {
        return bookingRepository.findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                userId, dateTime, Status.APPROVED, pageable).toList();
    }
}
