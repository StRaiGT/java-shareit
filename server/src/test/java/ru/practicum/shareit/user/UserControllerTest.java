package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @MockBean
    private UserService userService;

    private final UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@yandex.ru")
            .build();
    private final UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("Test user 2")
            .email("tester2@yandex.ru")
            .build();
    private UserDto userDtoToPatch;
    private UserDto userDtoPatched;

    @BeforeEach
    public void beforeEach() {
        userDtoToPatch = UserDto.builder()
                .name("Patched test user 1")
                .email("PatchedTester1@yandex.ru")
                .build();

        userDtoPatched = UserDto.builder()
                .id(1L)
                .name("Patched test user 1")
                .email("PatchedTester1@yandex.ru")
                .build();
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() throws Exception {
            when(userService.create(ArgumentMatchers.any(UserDto.class))).thenReturn(userDto1);

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(userDto1))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(userDto1)));

            verify(userService, times(1)).create(ArgumentMatchers.any(UserDto.class));
        }
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGet() throws Exception {
            when(userService.getAll()).thenReturn(List.of(userDto1, userDto2));

            mvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(userDto1, userDto2))));

            verify(userService, times(1)).getAll();
        }

        @Test
        public void shouldGetIfEmpty() throws Exception {
            when(userService.getAll()).thenReturn(List.of());

            mvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of())));

            verify(userService, times(1)).getAll();
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() throws Exception {
            when(userService.getById(ArgumentMatchers.eq(userDto1.getId()))).thenReturn(userDto1);

            mvc.perform(get("/users/{id}", userDto1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(userDto1)));

            verify(userService, times(1)).getById(ArgumentMatchers.eq(userDto1.getId()));
        }
    }

    @Nested
    class Patch {
        @Test
        public void shouldPatch() throws Exception {
            when(userService.patch(ArgumentMatchers.eq(userDtoPatched.getId()), ArgumentMatchers.any(UserDto.class)))
                    .thenReturn(userDtoPatched);

            mvc.perform(patch("/users/{id}", userDtoPatched.getId())
                            .content(mapper.writeValueAsString(userDtoToPatch))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(userDtoPatched)));

            verify(userService, times(1))
                    .patch(ArgumentMatchers.eq(userDtoPatched.getId()), ArgumentMatchers.any(UserDto.class));
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() throws Exception {
            mvc.perform(delete("/users/{id}", 99L))
                    .andExpect(status().isOk());

            verify(userService, times(1)).delete(99L);
        }
    }
}