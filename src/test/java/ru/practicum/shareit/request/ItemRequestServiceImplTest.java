package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestExtendedDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.controller.UserController;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapperImpl itemRequestMapper;

    @Mock
    private ItemMapperImpl itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    private static User user1;
    private static User user2;
    private static Item item1;
    private static LocalDateTime dateTime;
    private static ItemRequestCreateDto item1RequestCreateDto;
    private static ItemRequest itemRequest1;
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

        item1 = Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .owner(user1)
                .requestId(1L)
                .build();

        dateTime = LocalDateTime.of(2023,1,1,10,0,0);

        item1RequestCreateDto = ItemRequestCreateDto.builder()
                .description("item description")
                .build();

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("itemRequest1 description")
                .requesterId(user2)
                .created(dateTime)
                .items(List.of(item1))
                .build();

        final int from = Integer.parseInt(UserController.PAGE_DEFAULT_FROM);
        final int size = Integer.parseInt(UserController.PAGE_DEFAULT_SIZE);
        pageable = PageRequest.of(from / size, size);
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(itemRequestMapper.toItemRequest(any(), any(), any())).thenCallRealMethod();
            when(itemRequestRepository.save(any())).thenReturn(itemRequest1);
            when(itemRequestMapper.toItemRequestDto(any())).thenCallRealMethod();

            ItemRequestDto result = itemRequestService.create(user2.getId(), item1RequestCreateDto);

            verify(itemRequestRepository, times(1)).save(itemRequestArgumentCaptor.capture());
            verify(itemRequestMapper, times(1)).toItemRequest(any(), any(), any());

            ItemRequest savedItemRequest = itemRequestArgumentCaptor.getValue();
            savedItemRequest.setId(result.getId());

            assertEquals(itemRequest1, savedItemRequest);
            assertEquals(item1RequestCreateDto.getDescription(), savedItemRequest.getDescription());
            assertEquals(user2.getId(), savedItemRequest.getRequesterId().getId());
            assertEquals(user2.getName(), savedItemRequest.getRequesterId().getName());
            assertEquals(user2.getEmail(), savedItemRequest.getRequesterId().getEmail());
            assertNotNull(savedItemRequest.getCreated());
            assertNull(savedItemRequest.getItems());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
            when(itemMapper.toItemDto(any())).thenCallRealMethod();
            when(itemRequestMapper.toItemRequestExtendedDto(any(), any())).thenCallRealMethod();

            ItemRequestExtendedDto result = itemRequestService.getById(user2.getId(), 1L);

            assertEquals(itemRequest1.getId(), result.getId());
            assertEquals(itemRequest1.getDescription(), result.getDescription());
            assertEquals(dateTime, result.getCreated());
            assertNotNull(result.getItems());
            assertEquals(1, result.getItems().size());
            assertEquals(item1.getId(), result.getItems().get(0).getId());
            assertEquals(item1.getName(), result.getItems().get(0).getName());
            assertEquals(item1.getDescription(), result.getItems().get(0).getDescription());
            assertEquals(item1.getRequestId(), result.getItems().get(0).getRequestId());
            assertEquals(item1.getAvailable(), result.getItems().get(0).getAvailable());
            assertEquals(item1.getOwner().getId(), result.getItems().get(0).getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(itemRequestRepository, times(1)).findById(1L);
            verify(itemMapper, times(1)).toItemDto(any());
            verify(itemRequestMapper, times(1)).toItemRequestExtendedDto(any(), any());
        }

        @Test
        public void shouldThrowExceptionIfItemRequestIdNotFound() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () ->  itemRequestService.getById(user2.getId(), 1L));
            assertEquals("Запроса вещи с таким id не существует.", exception.getMessage());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(itemRequestRepository, times(1)).findById(1L);
        }
    }

    @Nested
    class GetByRequesterId {
        @Test
        public void shouldGet() {
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user2.getId()))
                    .thenReturn(List.of(itemRequest1));
            when(itemMapper.toItemDto(any())).thenCallRealMethod();
            when(itemRequestMapper.toItemRequestExtendedDto(any(), any())).thenCallRealMethod();

            List<ItemRequestExtendedDto> result = itemRequestService.getByRequesterId(user2.getId());

            assertEquals(1, result.size());
            assertEquals(itemRequest1.getId(), result.get(0).getId());
            assertEquals(itemRequest1.getDescription(), result.get(0).getDescription());
            assertEquals(itemRequest1.getCreated(), result.get(0).getCreated());
            assertNotNull(result.get(0).getItems());
            assertEquals(1, result.get(0).getItems().size());
            assertEquals(itemRequest1.getItems().get(0).getId(), result.get(0).getItems().get(0).getId());
            assertEquals(itemRequest1.getItems().get(0).getName(), result.get(0).getItems().get(0).getName());
            assertEquals(itemRequest1.getItems().get(0).getDescription(),
                    result.get(0).getItems().get(0).getDescription());
            assertEquals(itemRequest1.getItems().get(0).getAvailable(), result.get(0).getItems().get(0).getAvailable());
            assertEquals(itemRequest1.getItems().get(0).getRequestId(), result.get(0).getItems().get(0).getRequestId());
            assertEquals(itemRequest1.getItems().get(0).getOwner().getId(),
                    result.get(0).getItems().get(0).getOwnerId());
            verify(userService, times(1)).getUserById(user2.getId());
            verify(itemRequestRepository, times(1))
                    .findByRequesterId_IdOrderByCreatedAsc(user2.getId());
            verify(itemMapper, times(1)).toItemDto(any());
            verify(itemRequestMapper, times(1)).toItemRequestExtendedDto(any(), any());
        }

        @Test
        public void shouldGetEmptyIfNotItemRequests() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(itemRequestRepository.findByRequesterId_IdOrderByCreatedAsc(user1.getId()))
                    .thenReturn(List.of());

            List<ItemRequestExtendedDto> result = itemRequestService.getByRequesterId(user1.getId());

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(itemRequestRepository, times(1))
                    .findByRequesterId_IdOrderByCreatedAsc(user1.getId());
        }
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGetNotSelfRequests() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(itemRequestRepository.findByRequesterId_IdNot(user1.getId(), pageable))
                    .thenReturn(new PageImpl<>(List.of(itemRequest1)));
            when(itemMapper.toItemDto(any())).thenCallRealMethod();
            when(itemRequestMapper.toItemRequestExtendedDto(any(), any())).thenCallRealMethod();

            List<ItemRequestExtendedDto> result = itemRequestService.getAll(user1.getId(), pageable);

            assertEquals(1, result.size());
            assertEquals(itemRequest1.getId(), result.get(0).getId());
            assertEquals(itemRequest1.getDescription(), result.get(0).getDescription());
            assertEquals(itemRequest1.getCreated(), result.get(0).getCreated());
            assertEquals(itemRequest1.getItems().size(), result.get(0).getItems().size());
            assertEquals(itemRequest1.getItems().get(0).getId(), result.get(0).getItems().get(0).getId());
            assertEquals(itemRequest1.getItems().get(0).getName(), result.get(0).getItems().get(0).getName());
            assertEquals(itemRequest1.getItems().get(0).getAvailable(), result.get(0).getItems().get(0).getAvailable());
            assertEquals(itemRequest1.getItems().get(0).getDescription(),
                    result.get(0).getItems().get(0).getDescription());
            assertEquals(itemRequest1.getItems().get(0).getOwner().getId(), result.get(0).getItems().get(0).getOwnerId());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(itemRequestRepository, times(1))
                    .findByRequesterId_IdNot(user1.getId(), pageable);
            verify(itemMapper, times(1)).toItemDto(any());
            verify(itemRequestMapper, times(1)).toItemRequestExtendedDto(any(), any());
        }

        @Test
        public void shouldGetEmptyIfNotRequests() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(itemRequestRepository.findByRequesterId_IdNot(user1.getId(), pageable))
                    .thenReturn(new PageImpl<>(List.of()));

            List<ItemRequestExtendedDto> result = itemRequestService.getAll(user1.getId(), pageable);

            assertEquals(0, result.size());
            verify(userService, times(1)).getUserById(user1.getId());
            verify(itemRequestRepository, times(1))
                    .findByRequesterId_IdNot(user1.getId(), pageable);
        }
    }
}
