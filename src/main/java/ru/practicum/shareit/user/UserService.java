package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto create(UserDto userDto);

    UserDto patch(Long id, UserDto userDto);

    void delete(Long id);
}
