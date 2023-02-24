package ru.practicum.shareit.booking.chainSearcher.owner;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.chainSearcher.Searcher;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ChainSearcherByOwner {
    Searcher searcherAll = new SearcherByOwnerIdAndStateAll();
    Searcher searcherCurrent = searcherAll.setNext(new SearcherByOwnerIdAndStateCurrent());
    Searcher searcherPast = searcherCurrent.setNext(new SearcherByOwnerIdAndStatePast());
    Searcher searcherFuture = searcherPast.setNext(new SearcherByOwnerIdAndStateFuture());
    Searcher searcherWaiting = searcherFuture.setNext(new SearcherByOwnerIdAndStateWaiting());
    Searcher searcherRejected = searcherWaiting.setNext(new SearcherByOwnerIdAndStateRejected());

    public List<Booking> search(Long userId, State state, Pageable pageable,
                                LocalDateTime dateTime, BookingRepository bookingRepository) {
        return searcherAll.findAll(userId, state, pageable, dateTime, bookingRepository);
    }
}
