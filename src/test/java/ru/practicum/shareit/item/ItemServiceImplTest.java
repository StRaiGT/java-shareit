package ru.practicum.shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.TestConstrains;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapperImpl itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user1 = TestConstrains.getUser1();
    private final User user2 = TestConstrains.getUser2();
    private final Item item1 = TestConstrains.getItem1(user1);
    private final Item item2 = TestConstrains.getItem2(user2);
    private final Item item3 = TestConstrains.getItem3(user1);
    private final ItemDto patchItem1Dto = TestConstrains.getPatchItem1Dto();
    private final LocalDateTime dateTime = TestConstrains.getDateTime();
    private final Booking getBooking1 = TestConstrains.getBooking1(user2, item1, dateTime.minusHours(20));
    private final Booking getBooking2 = TestConstrains.getBooking2(user2, item1, dateTime.minusHours(15));
    private final Booking getBooking3 = TestConstrains.getBooking3(user2, item1, dateTime.plusHours(15));
    private final Booking getBooking4 = TestConstrains.getBooking4(user2, item1, dateTime.plusHours(20));
    private final CommentRequestDto comment1RequestDto = TestConstrains.getCommentRequestDto();
    private final Pageable pageable = TestConstrains.getPageable();

    @Nested
    class GetByOwnerId {
        @Test
        void shouldGetTwoItems() {
            when(itemRepository.findByOwnerIdOrderByIdAsc(any(), any())).thenReturn(new PageImpl<>(List.of(item1, item3)));
            when(itemMapper.toItemExtendedDto(any(), any(), any())).thenCallRealMethod();

            itemService.getByOwnerId(user1.getId(), pageable);

            verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(any(), any());
            verify(itemMapper, times(2)).toItemExtendedDto(any(), any(), any());
        }

        @Test
        void shouldGetZeroItems() {
            when(itemRepository.findByOwnerIdOrderByIdAsc(any(), any())).thenReturn(new PageImpl<>(List.of()));

            itemService.getByOwnerId(user1.getId(), pageable);

            verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(any(), any());
            verify(itemMapper, never()).toItemExtendedDto(any(), any(), any());
        }
    }

    @Nested
    class GetItemById {
        @Test
        void shouldGet() {
            when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));

            itemService.getItemById(item2.getId());

            verify(itemRepository, times(1)).findById(any());
        }

        @Test
        void shouldThrowExceptionIfItemIdNotFound() {
            when(itemRepository.findById(item2.getId())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () ->  itemService.getItemById(item2.getId()));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
            verify(itemRepository, times(1)).findById(any());
        }
    }

    @Nested
    class GetById {
        @Test
        void shouldGetByNotOwner() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(itemMapper.toItemExtendedDto(any(), any(), any())).thenCallRealMethod();

            ItemExtendedDto itemFromService = itemService.getById(user2.getId(), item1.getId());

            assertNull(itemFromService.getLastBooking());
            assertNull(itemFromService.getNextBooking());
            verify(itemRepository, times(1)).findById(any());
            verify(itemMapper, times(1)).toItemExtendedDto(any(), any(), any());
        }

        @Test
        void shouldGetByOwnerWithLastAndNextBookings() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(itemMapper.toItemExtendedDto(any(), any(), any())).thenCallRealMethod();
            when(bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(List.of(getBooking2, getBooking1));
            when(bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any()))
                    .thenReturn(List.of(getBooking3, getBooking4));
            when(itemMapper.bookingToBookingItemDto(any())).thenCallRealMethod();

            ItemExtendedDto itemFromService = itemService.getById(user1.getId(), item1.getId());

            assertNotNull(itemFromService.getLastBooking());
            assertEquals(getBooking2.getId(), itemFromService.getLastBooking().getId());
            assertEquals(getBooking2.getBooker().getId(), itemFromService.getLastBooking().getBookerId());
            assertEquals(getBooking2.getStart(), itemFromService.getLastBooking().getStart());
            assertEquals(getBooking2.getEnd(), itemFromService.getLastBooking().getEnd());
            assertNotNull(itemFromService.getNextBooking());
            assertEquals(getBooking3.getId(), itemFromService.getNextBooking().getId());
            assertEquals(getBooking3.getBooker().getId(), itemFromService.getNextBooking().getBookerId());
            assertEquals(getBooking3.getStart(), itemFromService.getNextBooking().getStart());
            assertEquals(getBooking3.getEnd(), itemFromService.getNextBooking().getEnd());

            verify(itemRepository, times(1)).findById(any());
            verify(itemMapper, times(1)).toItemExtendedDto(any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any());
            verify(itemMapper, times(2)).bookingToBookingItemDto(any());
        }

        @Test
        void shouldGetByOwnerWithEmptyLastAndNextBookings() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(itemMapper.toItemExtendedDto(any(), any(), any())).thenCallRealMethod();
            when(bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(List.of());
            when(bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any()))
                    .thenReturn(List.of());

            ItemExtendedDto itemFromService = itemService.getById(user1.getId(), item1.getId());

            assertNull(itemFromService.getLastBooking());
            assertNull(itemFromService.getNextBooking());

            verify(itemRepository, times(1)).findById(any());
            verify(itemMapper, times(1)).toItemExtendedDto(any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any());
            verify(itemMapper, never()).bookingToBookingItemDto(any());
        }
    }

    @Nested
    class Create {
        @Test
        void shouldCreate() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(itemMapper.toItemDto(any())).thenCallRealMethod();
            when(itemMapper.toItem(any(), any())).thenCallRealMethod();

            itemService.create(user1.getId(), itemMapper.toItemDto(item1));

            verify(userService, times(1)).getUserById(user1.getId());
            verify(itemRepository, times(1)).save(item1);
            verify(itemMapper, times(2)).toItemDto(any());
            verify(itemMapper, times(1)).toItem(any(), any());
        }
    }

    @Nested
    class Patch {
        @Test
        void shouldPatchByOwner() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

            itemService.patch(user1.getId(), item1.getId(), patchItem1Dto);

            verify(itemRepository, times(1)).findById(any());
            verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());

            Item savedItem = itemArgumentCaptor.getValue();

            assertEquals(item1.getId(), savedItem.getId());
            assertEquals(patchItem1Dto.getName(), savedItem.getName());
            assertEquals(patchItem1Dto.getDescription(), savedItem.getDescription());
            assertEquals(patchItem1Dto.getAvailable(), savedItem.getAvailable());
        }

        @Test
        void shouldThrowExceptionIfPatchByNotOwner() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

            ForbiddenException exception = assertThrows(ForbiddenException.class,
                    () ->  itemService.patch(user2.getId(), item1.getId(), patchItem1Dto));
            assertEquals("Изменение вещи доступно только владельцу.", exception.getMessage());
            verify(itemRepository, times(1)).findById(any());
            verify(itemRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {
        @Test
        void shouldDeleteIfIdExists() {
            itemService.delete(item1.getId());

            verify(itemRepository, times(1)).deleteById(item1.getId());
        }

        @Test
        void shouldDeleteIfIdNotExists() {
            itemService.delete(99L);

            verify(itemRepository, times(1)).deleteById(99L);
        }
    }

    @Nested
    class Search {
        @Test
        void shouldGetEmptyListIfTextIsEmpty() {
            List<ItemDto> itemsFromService = itemService.search("", pageable);

            assertEquals(0, itemsFromService.size());
            verify(itemRepository, never()).search(any(), any());
        }

        @Test
        void shouldGetEmptyListIfTextIsBlank() {
            List<ItemDto> itemsFromService = itemService.search(" ", pageable);

            assertEquals(0, itemsFromService.size());
            verify(itemRepository, never()).search(any(), any());
        }

        @Test
        void shouldGetIfTextNotBlank() {
            when(itemRepository.search("iTemS", pageable)).thenReturn(new PageImpl<>(List.of(item1, item2)));

            List<ItemDto> itemsFromService = itemService.search("iTemS", pageable);

            assertEquals(2, itemsFromService.size());
            verify(itemRepository, times(1)).search(any(), any());
        }
    }

    @Nested
    class AddComment {
        @Test
        void shouldAdd() {
            when(itemMapper.commentRequestDtoToComment(any(), any(), any(), any())).thenCallRealMethod();
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any()))
                    .thenReturn(List.of(getBooking1, getBooking2));

            itemService.addComment(user2.getId(), item1.getId(), comment1RequestDto);

            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any());
            verify(commentRepository, times(1)).save(any());
        }

        @Test
        void shouldThrowExceptionIfNotFinishedBooking() {
            when(itemMapper.commentRequestDtoToComment(any(), any(), any(), any())).thenCallRealMethod();
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any()))
                    .thenReturn(List.of());

            BookingException exception = assertThrows(BookingException.class,
                    () ->  itemService.addComment(user2.getId(), item1.getId(), comment1RequestDto));
            assertEquals("Пользователь не брал данную вещь в аренду.", exception.getMessage());

            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any());
            verify(commentRepository, never()).save(any());
        }
    }
}
