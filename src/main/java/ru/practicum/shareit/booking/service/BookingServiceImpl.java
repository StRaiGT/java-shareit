package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingResponseDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto getById(Long userId, Long id) {
        log.info("Вывод бронирования с id {}.", id);

        Booking booking = getBookingById(id);

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Просмотр бронирования доступно только автору или владельцу.");
        }

        return bookingMapper.bookingToBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllByBookerId(Long userId, State state) {
        log.info("Вывод всех бронирований пользователя {} и статусом {}.", userId, state);

        userService.getUserById(userId);

        List<Booking> bookings = null;
        LocalDateTime dateTime = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, dateTime, dateTime);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                        userId, dateTime, Status.APPROVED);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        userId, dateTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        userId, Status.REJECTED);
        }

        return bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByOwnerId(Long userId, State state) {
        log.info("Вывод всех вещей пользователя {} и статусом {}.", userId, state);

        userService.getUserById(userId);

        List<Booking> bookings = null;
        LocalDateTime dateTime = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, dateTime, dateTime);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeAndStatusEqualsOrderByStartDesc(
                        userId, dateTime, Status.APPROVED);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, dateTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                        userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                        userId, Status.REJECTED);
        }

        return bookings.stream()
                .map(bookingMapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto) {
        log.info("Создание бронирования {} пользователем с id {}.", bookingRequestDto, userId);

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new BookingException("Недопустимое время брони.");
        }

        Item item = itemService.getItemById(bookingRequestDto.getItemId());

        if (!item.getAvailable()) {
            throw new BookingException("Предмет недоступен для бронирования.");
        }

        User user = userService.getUserById(userId);

        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Владелец не может бронировать собственную вещь.");
        }

        Booking booking = bookingMapper.requestDtoToBooking(bookingRequestDto, item, user, Status.WAITING);
        return bookingMapper.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto patch(Long userId, Long id, Boolean approved) {
        log.info("Обновление статуса бронирования {}.", id);

        Booking repoBooking = getBookingById(id);

        if (!userId.equals(repoBooking.getItem().getOwner().getId())) {
            throw new NotFoundException("Изменение статуса бронирования доступно только владельцу.");
        }
        if (!repoBooking.getStatus().equals(Status.WAITING)) {
            throw new BookingException("Ответ по бронированию уже дан.");
        }

        repoBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.bookingToBookingResponseDto(bookingRepository.save(repoBooking));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не существует."));
    }
}
