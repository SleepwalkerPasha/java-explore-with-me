package ru.practicum.ewm.closed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.closed.dto.api.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.closed.dto.api.EventRequestStatusUpdateResult;
import ru.practicum.ewm.closed.dto.api.NewEvent;
import ru.practicum.ewm.closed.dto.api.ParticipationRequest;
import ru.practicum.ewm.closed.dto.api.RequestState;
import ru.practicum.ewm.closed.dto.api.StateAction;
import ru.practicum.ewm.closed.dto.api.UpdateEventRequest;
import ru.practicum.ewm.closed.repository.RequestRepository;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.entities.ParticipationRequestDto;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersEventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<EventShort> getUsersAddedEvents(long userId, int from, int size) {
        checkForUser(userId);
        log.info("private: get user id {} added events", userId);
        return eventRepository.getEventDtoByInitiatorId(userId, PageRequester.of(from, size))
                .stream()
                .map(EventMapper::toEventShort)
                .collect(Collectors.toList());
    }

    @Transactional
    public Event addNewEvent(long userId, NewEvent newEvent) {
        UserDto initiatorDto = checkForUser(userId);
        EventDto eventDto = EventMapper.toEventDto(newEvent);
        Long catId = newEvent.getCategory();
        eventDto.setCategory(checkForCategory(catId));
        eventDto.setInitiator(initiatorDto);
        eventDto.setCreatedOn(LocalDateTime.now());
        eventDto.setState(EventState.PENDING);
        eventDto.setConfirmedRequests(0L);
        log.info("private: add new event user id {}", userId);
        return EventMapper.toEvent(eventRepository.save(eventDto));
    }

    @Transactional(readOnly = true)
    public Event getUsersEventById(Long userId, Long eventId) {
        checkForUser(userId);
        Optional<EventDto> eventDtoByInitiatorIdAndId = eventRepository.findEventDtoByInitiator_IdAndId(userId, eventId);
        if (eventDtoByInitiatorIdAndId.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        log.info("private: get event user id {} event id {}", userId, eventId);
        return EventMapper.toEvent(eventDtoByInitiatorIdAndId.get());
    }

    @Transactional
    public Event updateEventInfo(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {
        UserDto userDto = checkForUser(userId);

        Optional<EventDto> eventDtoByInitiatorIdAndId = eventRepository.findEventDtoByInitiator_IdAndId(userId, eventId);
        if (eventDtoByInitiatorIdAndId.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        EventDto oldEvent = eventDtoByInitiatorIdAndId.get();
        if (oldEvent.getState().equals(EventState.PUBLISHED) ||
                Duration.between(oldEvent.getEventDate(), LocalDateTime.now()).abs().toMinutes() < 120) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        EventDto eventDto = EventMapper.toEventDto(updateEventRequest, oldEvent);
        String stateAction = updateEventRequest.getStateAction();
        if (stateAction != null) {
            Optional<StateAction> from = StateAction.from(stateAction);
            if (from.isEmpty()) {
                throw new ConflictException(String.format("invalid state %s", updateEventRequest.getStateAction()));
            }
            if (from.get().equals(StateAction.CANCEL_REVIEW)) {
                eventDto.setState(EventState.CANCELED);
            } else {
                eventDto.setState(EventState.PENDING);
                eventDto.setPublishedOn(LocalDateTime.now());
            }
        }
        eventDto.setId(eventId);
        Long catId = updateEventRequest.getCategory();
        if (catId != null) {
            eventDto.setCategory(checkForCategory(catId));
        } else {
            eventDto.setCategory(oldEvent.getCategory());
        }
        eventDto.setInitiator(userDto);
        eventDto.setCreatedOn(oldEvent.getCreatedOn());
        eventDto.setConfirmedRequests(oldEvent.getConfirmedRequests());
        log.info("private: update event user id {} event id {}", userId, eventId);
        return EventMapper.toEvent(eventRepository.save(eventDto));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequest> getRequestToUsersEvent(Long userId, Long eventId) {
        getUsersEventById(userId, eventId);
        log.info("private: get requests user id {} event id {}", userId, eventId);
        return requestRepository.findParticipationRequestDtoByEventIdAndInitiatorId(eventId, userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequestsToUsersEvent(Long userId,
                                                                     Long eventId,
                                                                     EventRequestStatusUpdateRequest updateRequest) {
        Event usersEventById = getUsersEventById(userId, eventId);
        List<ParticipationRequestDto> requests = requestRepository
                .findParticipationRequestDtosInRequestIds(updateRequest.getRequestIds());
        if (requests.isEmpty()) {
            throw new ConflictException("Only pending requests can be updated");
        }
        log.info("private: update requests userId {} eventId {}", userId, eventId);
        if (usersEventById.getParticipantLimit() == 0 || usersEventById.getRequestModeration().equals(false)) {
            return new EventRequestStatusUpdateResult(requests
                    .stream()
                    .map(ParticipationRequestMapper::toParticipationRequest)
                    .collect(Collectors.toList()),
                    new ArrayList<>());
        }
        int participantLimit = usersEventById.getParticipantLimit();
        if (usersEventById.getConfirmedRequests() >= participantLimit) {
            throw new ConflictException("The participant limit has been reached");
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (ParticipationRequestDto request : requests) {
            if (request.getEvent().getConfirmedRequests() < participantLimit) {
                Optional<RequestState> from = RequestState.from(updateRequest.getStatus());
                if (from.isEmpty()) {
                    throw new ConflictException(String.format("invalid state %s", updateRequest.getStatus()));
                }
                if (from.get().equals(RequestState.REJECTED)) {
                    request.setStatus(from.get());
                    rejectedRequests.add(request);
                } else if (from.get().equals(RequestState.CONFIRMED)) {
                    request.setStatus(from.get());
                    request.getEvent().setConfirmedRequests(request.getEvent().getConfirmedRequests() + 1L);
                    confirmedRequests.add(request);
                }
            } else {
                break;
            }
        }
        if (!confirmedRequests.isEmpty()) {
            requestRepository.saveAll(confirmedRequests);
            eventRepository.saveAll(confirmedRequests.stream().map(ParticipationRequestDto::getEvent).collect(Collectors.toList()));
        }
        if (!rejectedRequests.isEmpty()) {
            requestRepository.saveAll(rejectedRequests);
            eventRepository.saveAll(rejectedRequests.stream().map(ParticipationRequestDto::getEvent).collect(Collectors.toList()));
        }
        return new EventRequestStatusUpdateResult(confirmedRequests.stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList()),
                rejectedRequests.stream()
                        .map(ParticipationRequestMapper::toParticipationRequest)
                        .collect(Collectors.toList()));
    }

    private CategoryDto checkForCategory(long catId) {
        Optional<CategoryDto> categoryDtoOptional = categoryRepository.findById(catId);
        if (categoryDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }
        return categoryDtoOptional.get();
    }

    private UserDto checkForUser(long userId) {
        Optional<UserDto> userDtoOptional = userRepository.findById(userId);
        if (userDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        return userDtoOptional.get();
    }

}
