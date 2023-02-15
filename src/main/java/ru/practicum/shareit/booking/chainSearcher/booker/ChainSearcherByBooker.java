package ru.practicum.shareit.booking.chainSearcher.booker;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.chainSearcher.Searcher;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ChainSearcherByBooker {
    Searcher searcherAll = new SearcherByBookerIdAndStateAll();
    Searcher searcherCurrent = searcherAll.setNext(new SearcherByBookerIdAndStateCurrent());
    Searcher searcherPast = searcherCurrent.setNext(new SearcherByBookerIdAndStatePast());
    Searcher searcherFuture = searcherPast.setNext(new SearcherByBookerIdAndStateFuture());
    Searcher searcherWaiting = searcherFuture.setNext(new SearcherByBookerIdAndStateWaiting());
    Searcher searcherRejected = searcherWaiting.setNext(new SearcherByBookerIdAndStateRejected());

    public List<Booking> search(Long userId, State state, Pageable pageable,
                                LocalDateTime dateTime, BookingRepository bookingRepository) {
        return searcherAll.findAll(userId, state, pageable, dateTime, bookingRepository);
    }
}
