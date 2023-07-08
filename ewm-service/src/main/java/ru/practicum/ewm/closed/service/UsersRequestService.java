package ru.practicum.ewm.closed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.closed.repository.RequestRepository;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.closed.dto.api.ParticipationRequest;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.entities.ParticipationRequestDto;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersRequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    public List<ParticipationRequest> getParticipationRequestsByUserId(long userId) {
        checkForUser(userId);
        log.info("private: get requests userID {}", userId);
        return requestRepository.findParticipationRequestDtosByRequester_Id(userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList());
    }

    public ParticipationRequest createParticipationRequest(long userId, long eventId) {
        UserDto userDto = checkForUser(userId);
        EventDto eventDto = checkForEvent(eventId);

        if (eventDto.getInitiator().equals(userDto)) {
            throw new ConflictException("initiator cant add request to his events");
        }
        else if (eventDto.getPublishedOn().isAfter(LocalDateTime.now())) {
            throw new ConflictException("cant add request to unpublished event");
        }
        else if (requestRepository.findParticipationRequestDtosByEvent_Id(eventDto.getId()).size() >= eventDto.getParticipantLimit()) {
            throw new ConflictException("participation limit is reached");
        }
        else if (requestRepository.findParticipationRequestDtoByEvent_IdAndRequester_Id(userId, eventId).isPresent()) {
            throw new ConflictException("cant add more than one request");
        }

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(userDto);
        participationRequestDto.setEvent(eventDto);
        participationRequestDto.setStatus(EventState.PENDING);
        participationRequestDto.setCreated(LocalDateTime.now());
        log.info("private: create request userId {} eventId {}", userId, eventId);
        return ParticipationRequestMapper.toParticipationRequest(requestRepository.save(participationRequestDto));
    }

    public ParticipationRequest cancelParticipationRequest(long userId, long requestId) {
        checkForUser(userId);
        Optional<ParticipationRequestDto> requestDtoOptional = requestRepository.findById(requestId);
        if (requestDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Request with id=%d was not found",requestId));
        }
        ParticipationRequestDto participationRequestDto = requestDtoOptional.get();
        participationRequestDto.setStatus(EventState.CANCELED);
        log.info("private: cancel request userId {} requestId {}", userId, requestId);
        return ParticipationRequestMapper.toParticipationRequest(requestRepository.save(participationRequestDto));
    }

    private UserDto checkForUser(long userId) {
        Optional<UserDto> userDtoOptional = userRepository.findById(userId);
        if (userDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        return userDtoOptional.get();
    }

    private EventDto checkForEvent(long eventId) {
        Optional<EventDto> eventDtoOptional = eventRepository.findById(eventId);
        if (eventDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", eventId));
        }
        return eventDtoOptional.get();
    }


}
