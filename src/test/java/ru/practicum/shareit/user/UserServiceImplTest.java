package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
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
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private static User user1;
    private static User user2;

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
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGet() {
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));
            when(userMapper.toUserDto(any())).thenCallRealMethod();

            List<UserDto> usersFromService = userService.getAll();

            assertEquals(2, usersFromService.size());

            UserDto usersFromService1 = usersFromService.get(0);
            UserDto usersFromService2 = usersFromService.get(1);

            assertEquals(user1.getId(), usersFromService1.getId());
            assertEquals(user1.getName(), usersFromService1.getName());
            assertEquals(user1.getEmail(), usersFromService1.getEmail());

            assertEquals(user2.getId(), usersFromService2.getId());
            assertEquals(user2.getName(), usersFromService2.getName());
            assertEquals(user2.getEmail(), usersFromService2.getEmail());

            verify(userMapper, times(2)).toUserDto(any());
            verify(userRepository, times(1)).findAll();
        }

        @Test
        public void shouldGetIfEmpty() {
            when(userRepository.findAll()).thenReturn(new ArrayList<>());

            List<UserDto> usersFromService = userService.getAll();

            assertEquals(0, usersFromService.size());
            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
            when(userMapper.toUserDto(any())).thenCallRealMethod();

            UserDto userFromService = userService.getById(1L);

            assertEquals(user1.getId(), userFromService.getId());
            assertEquals(user1.getName(), userFromService.getName());
            assertEquals(user1.getEmail(), userFromService.getEmail());
            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).toUserDto(any());
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(99L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            verify(userRepository, times(1)).findById(any());
        }
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
            when(userMapper.toUserDto(any())).thenCallRealMethod();
            when(userMapper.toUser(any())).thenCallRealMethod();

            userService.create(userMapper.toUserDto(user1));

            verify(userRepository, times(1)).save(user1);
        }
    }

    @Nested
    class Patch {
        private UserDto patchUserDto;

        @BeforeEach
        public void beforeEachPatch() {
            patchUserDto = UserDto.builder()
                    .id(1L)
                    .name("Patch test user 1")
                    .email("tester2@yandex.ru")
                    .build();
        }

        @Test
        public void shouldPatch() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

            userService.patch(user1.getId(), patchUserDto);

            verify(userRepository, times(1)).save(userArgumentCaptor.capture());

            User savedUser = userArgumentCaptor.getValue();

            assertEquals(user1.getId(), savedUser.getId());
            assertEquals(patchUserDto.getName(), savedUser.getName());
            assertEquals(patchUserDto.getEmail(), savedUser.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> userService.patch(99L, patchUserDto));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            verify(userRepository, times(1)).findById(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            userService.delete(user1.getId());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(1L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            verify(userRepository, times(1)).deleteById(1L);
            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        public void shouldDeleteIfUserIdNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            userService.delete(99L);

            NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(99L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            verify(userRepository, times(1)).deleteById(99L);
            verify(userRepository, times(1)).findById(99L);
        }
    }

    @Nested
    class GetUserById {
        @Test
        public void shouldGet() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

            User userFromService = userService.getUserById(1L);

            assertEquals(user1.getId(), userFromService.getId());
            assertEquals(user1.getName(), userFromService.getName());
            assertEquals(user1.getEmail(), userFromService.getEmail());
            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(99L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            verify(userRepository, times(1)).findById(any());
        }
    }
}