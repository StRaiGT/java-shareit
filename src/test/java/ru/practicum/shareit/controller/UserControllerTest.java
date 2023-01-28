package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    private final UserController userController;

    @Nested
    class CreateUser {
        @Test
        public void shouldCreate() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto);

            List<UserDto> usersFromController = new ArrayList<>(userController.getAllUsers());

            assertEquals(usersFromController.size(), 1);

            UserDto userDtoFromController = usersFromController.get(0);

            assertEquals(userDtoFromController.getId(), userDto.getId());
            assertEquals(userDtoFromController.getName(), userDto.getName());
            assertEquals(userDtoFromController.getEmail(), userDto.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfExistedEmail() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester@yandex.ru")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> userController.createUser(userDto2));
            assertEquals(userController.getAllUsers().size(), 1);

            UserDto userDtoFromController = userController.getAllUsers().get(0);

            assertEquals(userDtoFromController.getId(), userDto1.getId());
            assertEquals(userDtoFromController.getName(), userDto1.getName());
            assertEquals(userDtoFromController.getEmail(), userDto1.getEmail());
        }
    }

    @Nested
    class GetAllUsers {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.createUser(userDto2);

            List<UserDto> usersFromController = userController.getAllUsers();

            assertEquals(usersFromController.size(), 2);

            UserDto userFromController1 = usersFromController.get(0);
            UserDto userFromController2 = usersFromController.get(1);

            assertEquals(userFromController1.getId(), userDto1.getId());
            assertEquals(userFromController1.getName(), userDto1.getName());
            assertEquals(userFromController1.getEmail(), userDto1.getEmail());

            assertEquals(userFromController2.getId(), userDto2.getId());
            assertEquals(userFromController2.getName(), userDto2.getName());
            assertEquals(userFromController2.getEmail(), userDto2.getEmail());
        }

        @Test
        public void shouldGetIfEmpty() {
            List<UserDto> usersFromController = userController.getAllUsers();

            assertEquals(usersFromController.size(), 0);
        }
    }

    @Nested
    class GetUserById {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto usersFromController = userController.getUserById(1L);

            assertEquals(usersFromController.getId(), userDto1.getId());
            assertEquals(usersFromController.getName(), userDto1.getName());
            assertEquals(usersFromController.getEmail(), userDto1.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getUserById(10L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            assertEquals(userController.getAllUsers().size(), 0);
        }
    }

    @Nested
    class PatchUser {
        @Test
        public void shouldPatch() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Patch test user 1")
                    .email("tester2@yandex.ru")
                    .build();
            userController.patchUser(userDto1.getId(), userDto2);

            List<UserDto> usersFromController = userController.getAllUsers();

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            assertEquals(userFromController.getId(), userDto1.getId());
            assertEquals(userFromController.getName(), userDto2.getName());
            assertEquals(userFromController.getEmail(), userDto2.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfExistedEmail() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.createUser(userDto2);

            UserDto userDto3 = UserDto.builder()
                    .id(3L)
                    .name("Patch test user 1")
                    .email("tester2@yandex.ru")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> userController.patchUser(userDto1.getId(), userDto3));

            List<UserDto> usersFromController = userController.getAllUsers();

            assertEquals(usersFromController.size(), 2);

            UserDto userFromController1 = usersFromController.get(0);
            UserDto userFromController2 = usersFromController.get(1);

            assertEquals(userFromController1.getId(), userDto1.getId());
            assertEquals(userFromController1.getName(), userDto1.getName());
            assertEquals(userFromController1.getEmail(), userDto1.getEmail());

            assertEquals(userFromController2.getId(), userDto2.getId());
            assertEquals(userFromController2.getName(), userDto2.getName());
            assertEquals(userFromController2.getEmail(), userDto2.getEmail());
        }
    }

    @Nested
    class DeleteUser {
        @Test
        public void shouldDelete() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.createUser(userDto1);

            List<UserDto> usersFromController = userController.getAllUsers();

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            assertEquals(userFromController.getId(), userDto1.getId());
            assertEquals(userFromController.getName(), userDto1.getName());
            assertEquals(userFromController.getEmail(), userDto1.getEmail());

            userController.deleteUser(userDto1.getId());

            assertEquals(userController.getAllUsers().size(), 0);
        }

        @Test
        public void shouldDeleteIfUserIdNotFound() {
            assertThrows(EmptyResultDataAccessException.class, () -> userController.deleteUser(10L));

            assertEquals(userController.getAllUsers().size(), 0);
        }
    }
}
