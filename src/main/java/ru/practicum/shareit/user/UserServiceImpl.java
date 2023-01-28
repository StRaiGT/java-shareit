package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Вывод всех пользователей.");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Вывод пользователя с id {}.", id);
        return userMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует.")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Добавление пользователя {}", userDto);
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto patchUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя {} с id {}.", userDto, id);

        User repoUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));

        userDto.setId(id);
        User user = userMapper.toUser(userDto);

        if (user.getEmail() != null) {
            repoUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            repoUser.setName(user.getName());
        }

        return userMapper.toUserDto(userRepository.save(repoUser));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id {}", id);
        userRepository.deleteById(id);
    }
}
