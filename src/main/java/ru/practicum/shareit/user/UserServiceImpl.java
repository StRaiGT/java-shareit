package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Вывод всех пользователей.");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Вывод пользователя с id {}.", id);
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Добавление пользователя {}", userDto);
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto patchUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя {} с id {}.", userDto, id);
        userDto.setId(id);
        return UserMapper.toUserDto(userRepository.patchUser(UserMapper.toUser(userDto)));
    }

    @Override
    public Boolean deleteUser(Long id) {
        log.info("Удаление пользователя с id {}", id);
        return userRepository.deleteUser(id);
    }
}
