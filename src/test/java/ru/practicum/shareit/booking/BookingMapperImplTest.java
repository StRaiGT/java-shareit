package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestConstrains;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingMapperImplTest {
    @Mock
    private UserMapperImpl userMapper;

    @Mock
    private ItemMapperImpl itemMapper;

    @InjectMocks
    private BookingMapperImpl bookingMapper;

    private final User user = TestConstrains.getUser1();
    private final Item item = TestConstrains.getItem1(user);
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final Status status = Status.WAITING;
    private final BookingRequestDto bookingRequestDto = TestConstrains.getBookingRequestDto(dateTime, item);
    private final Booking booking = TestConstrains.getBooking1(user, item, dateTime);

    @Nested
    class RequestDtoToBooking {
        @Test
        void shouldReturnBooking() {
            Booking result = bookingMapper.requestDtoToBooking(bookingRequestDto, item, user, status);

            assertNull(result.getId());
            assertEquals(status, result.getStatus());
            assertEquals(bookingRequestDto.getStart(), result.getStart());
            assertEquals(bookingRequestDto.getEnd(), result.getEnd());
            assertEquals(user.getId(), result.getBooker().getId());
            assertEquals(user.getName(), result.getBooker().getName());
            assertEquals(user.getEmail(), result.getBooker().getEmail());
            assertEquals(bookingRequestDto.getItemId(), result.getItem().getId());
            assertEquals(item.getDescription(), result.getItem().getDescription());
            assertEquals(item.getAvailable(), result.getItem().getAvailable());
            assertEquals(item.getName(), result.getItem().getName());
            assertEquals(item.getOwner().getId(), result.getItem().getOwner().getId());
            assertEquals(item.getOwner().getName(), result.getItem().getOwner().getName());
            assertEquals(item.getOwner().getEmail(), result.getItem().getOwner().getEmail());
        }

        @Test
        void shouldReturnNull() {
            Booking result = bookingMapper.requestDtoToBooking(null, null,
                    null, null);

            assertNull(result);
        }
    }

    @Nested
    class BookingToBookingResponseDto {
        @Test
        void shouldReturnBookingResponseDto() {
            when(userMapper.toUserDto(any())).thenCallRealMethod();
            when(itemMapper.toItemDto(any())).thenCallRealMethod();

            BookingResponseDto result = bookingMapper.bookingToBookingResponseDto(booking);

            assertEquals(booking.getId(), result.getId());
            assertEquals(booking.getStart(), result.getStart());
            assertEquals(booking.getEnd(), result.getEnd());
            assertEquals(booking.getStatus(), result.getStatus());
            assertEquals(booking.getBooker().getId(), result.getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.getBooker().getEmail());
            assertEquals(booking.getItem().getId(), result.getItem().getId());
            assertEquals(booking.getItem().getName(), result.getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.getItem().getAvailable());
            assertEquals(booking.getItem().getOwner().getId(), result.getItem().getOwnerId());
            assertEquals(booking.getItem().getRequestId(), result.getItem().getRequestId());
            verify(userMapper, times(1)).toUserDto(any());
            verify(itemMapper, times(1)).toItemDto(any());
        }

        @Test
        void shouldReturnNull() {
            BookingResponseDto result = bookingMapper.bookingToBookingResponseDto(null);

            assertNull(result);
        }
    }
}
