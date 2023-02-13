package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {
    @InjectMocks
    private UserMapperImpl userMapper;

    private static User user;
    private static UserDto userDto;

    @BeforeAll
    public static void beforeAll() {
        user = User.builder()
                .id(1L)
                .name("Test user 1")
                .email("tester1@yandex.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test user 1")
                .email("tester1@yandex.ru")
                .build();
    }

    @Nested
    class ToUserDto {
        @Test
        public void shouldReturnUserDto() {
            UserDto result = userMapper.toUserDto(user);

            assertEquals(user.getId(), result.getId());
            assertEquals(user.getName(), result.getName());
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        public void shouldReturnNull() {
            UserDto result = userMapper.toUserDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToUser {
        @Test
        public void shouldReturnUser() {
            User result = userMapper.toUser(userDto);

            assertEquals(userDto.getId(), result.getId());
            assertEquals(userDto.getName(), result.getName());
            assertEquals(userDto.getEmail(), result.getEmail());
        }

        @Test
        public void shouldReturnNull() {
            User result = userMapper.toUser(null);

            assertNull(result);
        }
    }
}