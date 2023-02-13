package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.user.controller.UserController;
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

    private static User user1;
    private static User user2;
    private static User user3;
    private static Item item1;
    private static LocalDateTime dateTime;
    private static Item itemIsNoAvailable;
    private static Booking booking;
    private static BookingRequestDto bookingRequestDto;
    private static BookingRequestDto bookingRequestDtoWrongDate;
    private static BookingResponseDto bookingResponseDto;
    private static Pageable pageable;

    @BeforeAll
    public static void beforeAll() {
        user1 = User.builder()
                .id(1L)
                .name("Test user 1")
                .email("tester1@yandex.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Test user 2")
                .email("tester2@yandex.ru")
                .build();

        user3 = User.builder()
                .id(3L)
                .name("Test user 3")
                .email("tester3@yandex.ru")
                .build();

        UserDto user2Dto = UserDto.builder()
                .id(2L)
                .name("Test user 2")
                .email("tester2@yandex.ru")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item1 name")
                .description("seaRch1 description ")
                .available(true)
                .owner(user1)
                .build();

        ItemDto item1Dto = ItemDto.builder()
                .id(1L)
                .name("item1 name")
                .description("seaRch1 description ")
                .available(true)
                .ownerId(user1.getId())
                .build();

        itemIsNoAvailable = Item.builder()
                .id(3L)
                .name("item3 name")
                .description("itEm3 description")
                .available(false)
                .owner(user1)
                .build();

        dateTime = LocalDateTime.of(2023,1,1,10,0,0);

        booking = Booking.builder()
                .id(1L)
                .start(dateTime.minusYears(10))
                .end(dateTime.minusYears(9))
                .item(item1)
                .booker(user2)
                .status(Status.APPROVED)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .start(dateTime.plusYears(5))
                .end(dateTime.plusYears(6))
                .itemId(item1.getId())
                .build();

        bookingRequestDtoWrongDate = BookingRequestDto.builder()
                .start(dateTime.plusYears(5))
                .end(dateTime)
                .itemId(item1.getId())
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(dateTime.minusYears(10))
                .end(dateTime.minusYears(9))
                .item(item1Dto)
                .booker(user2Dto)
                .status(Status.APPROVED)
                .build();

        final int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        final int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        pageable = PageRequest.of(from / size, size);
    }

    @Nested
    class GetById {
        @Test
        public void shouldGetByAuthor() {
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
        public void shouldGetByOwner() {
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
        public void shouldThrowExceptionIfNotOwnerOrAuthor() {
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
        public void shouldGetAllIfBooker() {
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
        public void shouldGetAllEmptyIfNotBooker() {
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
        public void shouldGetCurrentIfBooker() {
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
        public void shouldGetCurrentEmptyIfNotBooker() {
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
        public void shouldGetPastIfBooker() {
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
        public void shouldGetPastEmptyIfNotBooker() {
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
        public void shouldGetFutureIfBooker() {
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
        public void shouldGetFutureEmptyIfNotBooker() {
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
        public void shouldGetWaitingIfBooker() {
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
        public void shouldGetWaitingEmptyIfNotBooker() {
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
        public void shouldGetRejectedIfBooker() {
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
        public void shouldGetRejectedEmptyIfNotBooker() {
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
        public void shouldGetAllIfOwner() {
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
        public void shouldGetAllEmptyIfNotBooker() {
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
        public void shouldGetCurrentIfOwner() {
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
        public void shouldGetCurrentEmptyIfNotBooker() {
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
        public void shouldGetPastIfOwner() {
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
        public void shouldGetPastEmptyIfNotBooker() {
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
        public void shouldGetFutureIfOwner() {
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
        public void shouldGetFutureEmptyIfNotBooker() {
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
        public void shouldGetWaitingIfOwner() {
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
        public void shouldGetWaitingEmptyIfNotBooker() {
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
        public void shouldGetRejectedIfOwner() {
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
        public void shouldGetRejectedEmptyIfNotBooker() {
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
        public void shouldCreate() {
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

            assertEquals(booking, savedBooking);
            assertEquals(booking.getId(), savedBooking.getId());
            assertEquals(booking.getStatus(), savedBooking.getStatus());
            assertEquals(booking.getStart(), savedBooking.getStart());
            assertEquals(booking.getEnd(), savedBooking.getEnd());
            assertEquals(booking.getBooker().getId(), savedBooking.getBooker().getId());
        }

        @Test
        public void shouldThrowExceptionIfEndIsBeforeStart() {
            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingService.create(user2.getId(), bookingRequestDtoWrongDate));
            assertEquals("Недопустимое время брони.", exception.getMessage());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        public void shouldThrowExceptionIfItemIsNotAvailable() {
            when(itemService.getItemById(bookingRequestDto.getItemId())).thenReturn(itemIsNoAvailable);

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingService.create(user2.getId(), bookingRequestDto));
            assertEquals("Предмет недоступен для бронирования.", exception.getMessage());
            verify(itemService, times(1)).getItemById(bookingRequestDto.getItemId());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        public void shouldThrowExceptionIfBookingByOwner() {
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
        private Booking bookingIsWaiting1;

        @BeforeEach
        public void beforeEachPatch() {
            bookingIsWaiting1 = Booking.builder()
                    .id(3L)
                    .start(dateTime.plusYears(8))
                    .end(dateTime.plusYears(9))
                    .item(item1)
                    .booker(user2)
                    .status(Status.WAITING)
                    .build();
        }

        @Test
        public void shouldApprove() {
            when(bookingRepository.findById(bookingIsWaiting1.getId())).thenReturn(Optional.of(bookingIsWaiting1));

            bookingService.patch(user1.getId(), bookingIsWaiting1.getId(), true);

            verify(bookingRepository, times(1)).findById(bookingIsWaiting1.getId());
            verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());

            Booking savedBooking = bookingArgumentCaptor.getValue();

            assertEquals(Status.APPROVED, savedBooking.getStatus());
        }

        @Test
        public void shouldReject() {
            when(bookingRepository.findById(bookingIsWaiting1.getId())).thenReturn(Optional.of(bookingIsWaiting1));

            bookingService.patch(user1.getId(), bookingIsWaiting1.getId(), false);

            verify(bookingRepository, times(1)).findById(bookingIsWaiting1.getId());
            verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());

            Booking savedBooking = bookingArgumentCaptor.getValue();

            assertEquals(Status.REJECTED, savedBooking.getStatus());
        }

        @Test
        public void shouldThrowExceptionIfPatchNotOwner() {
            when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingService.patch(user2.getId(), booking.getId(), false));
            assertEquals("Изменение статуса бронирования доступно только владельцу.", exception.getMessage());
            verify(bookingRepository, times(1)).findById(booking.getId());
            verify(bookingRepository, never()).save(any());
        }

        @Test
        public void shouldThrowExceptionIfAlreadyPatchBefore() {
            when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

            BookingException exception = assertThrows(BookingException.class,
                    () -> bookingService.patch(user1.getId(), booking.getId(), false));
            assertEquals("Ответ по бронированию уже дан.", exception.getMessage());
            verify(bookingRepository, times(1)).findById(booking.getId());
            verify(bookingRepository, never()).save(any());
        }
    }
}
