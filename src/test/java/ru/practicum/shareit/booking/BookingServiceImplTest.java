package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.TestConstrains;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapperImpl bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private final User user1 = TestConstrains.getUser1();
    private final User user2 = TestConstrains.getUser2();
    private final User user3 = TestConstrains.getUser3();
    private final UserDto user2Dto = TestConstrains.getUser2Dto();
    private final Item item1 = TestConstrains.getItem1(user1);
    private final ItemDto item1Dto = TestConstrains.getItem1Dto(user1);
    private final Item itemIsNoAvailable = TestConstrains.getItem3(user1);
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final Booking booking = TestConstrains.getBooking1(user2, item1, dateTime);
    private final Booking bookingIsWaiting = TestConstrains.getBooking3(user2, item1, dateTime);
    private final BookingRequestDto bookingRequestDto = TestConstrains.getBookingRequestDto(dateTime, item1);
    private final BookingRequestDto bookingRequestDtoWrongDate = TestConstrains.getBookingRequestDtoWrongDate(
            dateTime, item1);
    private final BookingResponseDto bookingResponseDto = TestConstrains.getBooking1ResponseDto(user2Dto, item1Dto,
            dateTime);
    private final Pageable pageable = TestConstrains.getPageable();

    @Nested
    class GetById {
        @Test
        void shouldGetByAuthor() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            BookingResponseDto result = bookingService.getById(user2.getId(), booking.getId());

            assertEquals(booking.getId(), result.getId());
            assertEquals(booking.getStart(), result.getStart());
            assertEquals(booking.getEnd(), result.getEnd());
            assertEquals(booking.getBooker().getId(), result.getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.getBooker().getEmail());
            assertEquals(booking.getStatus(), result.getStatus());
            assertEquals(booking.getItem().getId(), result.getItem().getId());
            assertEquals(booking.getItem().getName(), result.getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.getItem().getOwnerId());
            verify(bookingRepository, times(1)).findById(1L);
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetByOwner() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            BookingResponseDto result = bookingService.getById(user1.getId(), booking.getId());

            assertEquals(booking.getId(), result.getId());
            assertEquals(booking.getStart(), result.getStart());
            assertEquals(booking.getEnd(), result.getEnd());
            assertEquals(booking.getBooker().getId(), result.getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.getBooker().getEmail());
            assertEquals(booking.getStatus(), result.getStatus());
            assertEquals(booking.getItem().getId(), result.getItem().getId());
            assertEquals(booking.getItem().getName(), result.getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.getItem().getOwnerId());
            verify(bookingRepository, times(1)).findById(1L);
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldThrowExceptionIfNotOwnerOrAuthor() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingService.getById(user3.getId(), booking.getId()));
            assertEquals("Просмотр бронирования доступно только автору или владельцу.", exception.getMessage());
            verify(bookingRepository, times(1)).findById(1L);
        }
    }

    @Nested
    class GetAllByBookerId {
        @Test
        void shouldGetAllIfBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByBookerIdOrderByStartDesc(user2.getId(), pageable))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user2.getId(), State.ALL, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdOrderByStartDesc(user2.getId(), pageable);
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetAllEmptyIfNotBooker() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByBookerIdOrderByStartDesc(user1.getId(), pageable))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user1.getId(), State.ALL, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdOrderByStartDesc(user1.getId(), pageable);
        }

        @Test
        void shouldGetCurrentIfBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user2.getId(), State.CURRENT, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetCurrentEmptyIfNotBooker() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user1.getId(), State.CURRENT, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any());
        }

        @Test
        void shouldGetPastIfBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user2.getId(), State.PAST, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetPastEmptyIfNotBooker() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user1.getId(), State.PAST, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any());
        }

        @Test
        void shouldGetFutureIfBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user2.getId(), State.FUTURE, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetFutureEmptyIfNotBooker() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user1.getId(), State.FUTURE, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        }

        @Test
        void shouldGetWaitingIfBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user2.getId(), State.WAITING, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetWaitingEmptyIfNotBooker() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user1.getId(), State.WAITING, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
        }

        @Test
        void shouldGetRejectedIfBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user2.getId(), State.REJECTED, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetRejectedEmptyIfNotBooker() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByBookerId(user1.getId(), State.REJECTED, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
        }
    }

    @Nested
    class GetAllByOwnerId {
        @Test
        void shouldGetAllIfOwner() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByItemOwnerIdOrderByStartDesc(user1.getId(), pageable))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user1.getId(), State.ALL, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdOrderByStartDesc(user1.getId(), pageable);
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetAllEmptyIfNotBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemOwnerIdOrderByStartDesc(user2.getId(), pageable))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user2.getId(), State.ALL, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdOrderByStartDesc(user2.getId(), pageable);
        }

        @Test
        void shouldGetCurrentIfOwner() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user1.getId(), State.CURRENT, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetCurrentEmptyIfNotBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user2.getId(), State.CURRENT, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any());
        }

        @Test
        void shouldGetPastIfOwner() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user1.getId(), State.PAST, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetPastEmptyIfNotBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user2.getId(), State.PAST, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any(), any());
        }

        @Test
        void shouldGetFutureIfOwner() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user1.getId(), State.FUTURE, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetFutureEmptyIfNotBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user2.getId(), State.FUTURE, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        }

        @Test
        void shouldGetWaitingIfOwner() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user1.getId(), State.WAITING, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetWaitingEmptyIfNotBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user2.getId(), State.WAITING, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
        }

        @Test
        void shouldGetRejectedIfOwner() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            when(bookingMapper.bookingToBookingResponseDto(booking)).thenReturn(bookingResponseDto);

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user1.getId(), State.REJECTED, pageable);

            assertEquals(1, result.size());
            assertEquals(booking.getId(), result.get(0).getId());
            assertEquals(booking.getStart(), result.get(0).getStart());
            assertEquals(booking.getEnd(), result.get(0).getEnd());
            assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
            assertEquals(booking.getBooker().getName(), result.get(0).getBooker().getName());
            assertEquals(booking.getBooker().getEmail(), result.get(0).getBooker().getEmail());
            assertEquals(booking.getStatus(), result.get(0).getStatus());
            assertEquals(booking.getItem().getId(), result.get(0).getItem().getId());
            assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
            assertEquals(booking.getItem().getDescription(), result.get(0).getItem().getDescription());
            assertEquals(booking.getItem().getAvailable(), result.get(0).getItem().getAvailable());
            assertEquals(booking.getItem().getRequestId(), result.get(0).getItem().getRequestId());
            assertEquals(booking.getItem().getOwner().getId(), result.get(0).getItem().getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingMapper, times(1)).bookingToBookingResponseDto(booking);
        }

        @Test
        void shouldGetRejectedEmptyIfNotBooker() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of()));

            List<BookingResponseDto> result =  bookingService.getAllByOwnerId(user2.getId(), State.REJECTED, pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any());
        }
    }

    @Nested
    class Create {
        @Test
        void shouldCreate() {
            when(itemService.getItemById(bookingRequestDto.getItemId())).thenReturn(item1);
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingMapper.requestDtoToBooking(bookingRequestDto, item1, user2, Status.WAITING))
                    .thenReturn(booking);

            bookingService.create(user2.getId(), bookingRequestDto);

            verify(itemService, times(1)).getItemById(bookingRequestDto.getItemId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingMapper, times(1))
                    .requestDtoToBooking(bookingRequestDto, item1, user2, Status.WAITING);
            verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());

            Booking savedBooking = bookingArgumentCaptor.getValue();

            assertEquals(booking.getId(), savedBooking.getId());
            assertEquals(booking.getStatus(), savedBooking.getStatus());
            assertEquals(booking.getStart(), savedBooking.getStart());
            assertEquals(booking.getEnd(), savedBooking.getEnd());
            assertEquals(booking.getBooker().getId(), savedBooking.getBooker().getId());
        }

        @Test
        void shouldThrowExceptionIfEndIsBeforeStart() {
            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingService.create(user2.getId(), bookingRequestDtoWrongDate));
            assertEquals("Недопустимое время брони.", exception.getMessage());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionIfItemIsNotAvailable() {
            when(itemService.getItemById(bookingRequestDto.getItemId())).thenReturn(itemIsNoAvailable);

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingService.create(user2.getId(), bookingRequestDto));
            assertEquals("Предмет недоступен для бронирования.", exception.getMessage());
            verify(itemService, times(1)).getItemById(bookingRequestDto.getItemId());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionIfBookingByOwner() {
            when(itemService.getItemById(bookingRequestDto.getItemId())).thenReturn(item1);
            when(userService.getUserById(user1.getId())).thenReturn(user1);

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingService.create(user1.getId(), bookingRequestDto));
            assertEquals("Владелец не может бронировать собственную вещь.", exception.getMessage());
            verify(itemService, times(1)).getItemById(bookingRequestDto.getItemId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(bookingRepository, never()).save(any());
        }
    }

    @Nested
    class Patch {
        @Test
        void shouldApprove() {
            when(bookingRepository.findById(bookingIsWaiting.getId())).thenReturn(Optional.of(bookingIsWaiting));

            bookingService.patch(user1.getId(), bookingIsWaiting.getId(), true);

            verify(bookingRepository, times(1)).findById(bookingIsWaiting.getId());
            verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());

            Booking savedBooking = bookingArgumentCaptor.getValue();

            assertEquals(Status.APPROVED, savedBooking.getStatus());
        }

        @Test
        void shouldReject() {
            when(bookingRepository.findById(bookingIsWaiting.getId())).thenReturn(Optional.of(bookingIsWaiting));

            bookingService.patch(user1.getId(), bookingIsWaiting.getId(), false);

            verify(bookingRepository, times(1)).findById(bookingIsWaiting.getId());
            verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());

            Booking savedBooking = bookingArgumentCaptor.getValue();

            assertEquals(Status.REJECTED, savedBooking.getStatus());
        }

        @Test
        void shouldThrowExceptionIfPatchNotOwner() {
            when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingService.patch(user2.getId(), booking.getId(), false));
            assertEquals("Изменение статуса бронирования доступно только владельцу.", exception.getMessage());
            verify(bookingRepository, times(1)).findById(booking.getId());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionIfAlreadyPatchBefore() {
            when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingService.patch(user1.getId(), booking.getId(), false));
            assertEquals("Ответ по бронированию уже дан.", exception.getMessage());
            verify(bookingRepository, times(1)).findById(booking.getId());
            verify(bookingRepository, never()).save(any());
        }
    }
}
