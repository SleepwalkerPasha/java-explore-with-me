package ru.practicum.ewm.closed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.closed.dto.api.ParticipationRequest;
import ru.practicum.ewm.closed.dto.api.RequestState;
import ru.practicum.ewm.closed.repository.RequestRepository;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.entities.ParticipationRequestDto;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

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

    @Transactional(readOnly = true)
    public List<ParticipationRequest> getParticipationRequestsByUserId(long userId) {
        checkForUser(userId);
        log.info("private: get requests userID {}", userId);
        return requestRepository.findParticipationRequestDtosByRequester_Id(userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequest createParticipationRequest(long userId, long eventId) {
        UserDto userDto = checkForUser(userId);
        EventDto eventDto = checkForEvent(eventId);
        if (eventDto.getInitiator().getId().equals(userDto.getId())) {
            throw new ConflictException("initiator cant add request to his events");
        } else if (!eventDto.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("cant add request to unpublished event");
        } else if (eventDto.getParticipantLimit() != 0
                && eventDto.getConfirmedRequests().equals(eventDto.getParticipantLimit().longValue())) {
            throw new ConflictException("participation limit is reached");
        } else if (requestRepository.findParticipationRequestDtoByEvent_IdAndRequester_Id(eventId, userId).isPresent()) {
            throw new ConflictException("cant add more than one request");
        }
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(userDto);
        participationRequestDto.setEvent(eventDto);
        if (eventDto.getParticipantLimit() == 0) {
            participationRequestDto.setStatus(RequestState.CONFIRMED);
        } else {
            participationRequestDto.setStatus(RequestState.PENDING);
        }
        if (!eventDto.getRequestModeration()) {
            eventDto.setConfirmedRequests(eventDto.getConfirmedRequests() + 1L);
            eventRepository.save(eventDto);
        }
        participationRequestDto.setCreated(LocalDateTime.now());
        log.info("private: create request userId {} eventId {}", userId, eventId);
        return ParticipationRequestMapper.toParticipationRequest(requestRepository.save(participationRequestDto));
    }

    @Transactional
    public ParticipationRequest cancelParticipationRequest(long userId, long requestId) {
        checkForUser(userId);
        Optional<ParticipationRequestDto> requestDtoOptional = requestRepository.findById(requestId);
        if (requestDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
        }
        ParticipationRequestDto participationRequestDto = requestDtoOptional.get();
        if (participationRequestDto.getStatus().equals(RequestState.CONFIRMED)) {
            EventDto event = participationRequestDto.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        participationRequestDto.setStatus(RequestState.CANCELED);
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
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        return eventDtoOptional.get();
    }


}
