package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();
    User getUserById(Long id);
    User createUser(User user);
    User patchUser(User user);
    Boolean deleteUser(Long id);
}
