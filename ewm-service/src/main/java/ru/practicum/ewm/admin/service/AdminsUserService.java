package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.admin.dto.api.NewUserRequest;
import ru.practicum.ewm.admin.dto.api.UserFull;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.UserMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminsUserService {

    private final UserRepository userRepository;

    public List<UserFull> getUsersInUsersIds(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequester.of(from, size);
        log.info("admin: get users");
        return userRepository.findUserDtosByIds(ids, pageable)
                .stream()
                .map(UserMapper::toUserFull)
                .collect(Collectors.toList());
    }

    public UserFull createUser(NewUserRequest userRequest) {
        UserDto userDto = new UserDto();
        userDto.setName(userRequest.getName());
        userDto.setEmail(userRequest.getEmail());
        log.info("admin: create user");
        return UserMapper.toUserFull(userRepository.save(userDto));
    }

    public void deleteUser(Long userId) {
        Optional<UserDto> userDtoOptional = userRepository.findById(userId);
        if (userDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        userRepository.deleteById(userId);
        log.info("admin: delete user {}", userId);
    }
}
