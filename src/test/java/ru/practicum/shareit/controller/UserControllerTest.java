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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;

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
    class Create {
        @Test
        public void shouldCreate() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto);

            List<UserDto> usersFromController = new ArrayList<>(userController.getAll());

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            assertEquals(userFromController.getId(), userDto.getId());
            assertEquals(userFromController.getName(), userDto.getName());
            assertEquals(userFromController.getEmail(), userDto.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfExistedEmail() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester@yandex.ru")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> userController.create(userDto2));
            assertEquals(userController.getAll().size(), 1);

            UserDto userFromController = userController.getAll().get(0);

            assertEquals(userFromController.getId(), userDto1.getId());
            assertEquals(userFromController.getName(), userDto1.getName());
            assertEquals(userFromController.getEmail(), userDto1.getEmail());
        }
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            List<UserDto> usersFromController = userController.getAll();

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
            List<UserDto> usersFromController = userController.getAll();

            assertEquals(usersFromController.size(), 0);
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto usersFromController = userController.getById(1L);

            assertEquals(usersFromController.getId(), userDto1.getId());
            assertEquals(usersFromController.getName(), userDto1.getName());
            assertEquals(usersFromController.getEmail(), userDto1.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getById(10L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            assertEquals(userController.getAll().size(), 0);
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldPatch() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Patch test user 1")
                    .email("tester2@yandex.ru")
                    .build();
            userController.patch(userDto1.getId(), userDto2);

            List<UserDto> usersFromController = userController.getAll();

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
            userController.create(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@yandex.ru")
                    .build();
            userController.create(userDto2);

            UserDto userDto3 = UserDto.builder()
                    .id(3L)
                    .name("Patch test user 1")
                    .email("tester2@yandex.ru")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> userController.patch(userDto1.getId(), userDto3));

            List<UserDto> usersFromController = userController.getAll();

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
    class Delete {
        @Test
        public void shouldDelete() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@yandex.ru")
                    .build();
            userController.create(userDto1);

            List<UserDto> usersFromController = userController.getAll();

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            assertEquals(userFromController.getId(), userDto1.getId());
            assertEquals(userFromController.getName(), userDto1.getName());
            assertEquals(userFromController.getEmail(), userDto1.getEmail());

            userController.delete(userDto1.getId());

            assertEquals(userController.getAll().size(), 0);
        }

        @Test
        public void shouldDeleteIfUserIdNotFound() {
            assertThrows(EmptyResultDataAccessException.class, () -> userController.delete(10L));

            assertEquals(userController.getAll().size(), 0);
        }
    }
}
