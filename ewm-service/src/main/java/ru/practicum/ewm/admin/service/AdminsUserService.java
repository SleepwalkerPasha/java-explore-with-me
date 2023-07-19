package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.dto.api.NewUserRequest;
import ru.practicum.ewm.admin.dto.api.UserFull;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.UserMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminsUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserFull> getUsersInUsersIds(String ids, int from, int size) {
        Pageable pageable = PageRequester.of(from, size);
        List<Long> idsArray;
        log.info("admin: get users");
        if (ids != null && !ids.isBlank()) {
            idsArray = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
            return userRepository.findUserDtosByIds(idsArray, pageable)
                    .stream()
                    .map(UserMapper::toUserFull)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findUserDtosByIds(pageable).stream().map(UserMapper::toUserFull).collect(Collectors.toList());
        }
    }

    @Transactional
    public UserFull createUser(NewUserRequest userRequest) {
        Optional<UserDto> optionalUserDto = userRepository.getUserDtoByEmailAndName(userRequest.getEmail(), userRequest.getName());
        if (optionalUserDto.isPresent()) {
            throw new ConflictException("user already exist");
        }
        UserDto userDto = new UserDto();
        userDto.setName(userRequest.getName());
        userDto.setEmail(userRequest.getEmail());
        log.info("admin: create user");
        return UserMapper.toUserFull(userRepository.save(userDto));
    }

    @Transactional
    public void deleteUser(Long userId) {
        Optional<UserDto> userDtoOptional = userRepository.findById(userId);
        if (userDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        userRepository.deleteById(userId);
        log.info("admin: delete user {}", userId);
    }
}
