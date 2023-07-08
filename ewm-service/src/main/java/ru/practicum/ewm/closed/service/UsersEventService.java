package ru.practicum.ewm.closed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.closed.repository.RequestRepository;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.closed.dto.api.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.closed.dto.api.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.closed.dto.api.NewEvent;
import ru.practicum.ewm.closed.dto.api.ParticipationRequest;
import ru.practicum.ewm.closed.dto.api.StateAction;
import ru.practicum.ewm.closed.dto.api.UpdateEventUserRequest;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.entities.ParticipationRequestDto;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.CategoryMapper;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersEventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    public List<EventShort> getUsersAddedEvents(long userId, int from, int size) {
        checkForUser(userId);
        return eventRepository.getEventDtoByInitiator_Id(userId, PageRequester.of(from, size))
                .stream()
                .map(EventMapper::toEventShort)
                .collect(Collectors.toList());
    }

    public Event addNewEvent(long userId, NewEvent newEvent) {
        UserDto initiatorDto = checkForUser(userId);
        EventDto eventDto = EventMapper.toEventDto(newEvent);
        Long catId = newEvent.getCategory();
        Optional<CategoryDto> categoryDtoOptional = categoryRepository.findById(newEvent.getCategory());
        if (categoryDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }
        eventDto.setCategory(categoryDtoOptional.get());
        eventDto.setInitiator(initiatorDto);
        eventDto.setCreatedOn(LocalDateTime.now());
        eventDto.setState(EventState.PENDING);
        eventDto.setConfirmedRequests(0L);
        eventDto.setViews(0L);
        return EventMapper.toEvent(eventRepository.save(eventDto));
    }

    private UserDto checkForUser(long userId) {
        Optional<UserDto> userDtoOptional = userRepository.findById(userId);
        if (userDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        return userDtoOptional.get();
    }


    public Event getUsersEventById(Long userId, Long eventId) {
        checkForUser(userId);
        Optional<EventDto> eventDtoByInitiatorIdAndId = eventRepository.findEventDtoByInitiator_IdAndId(userId, eventId);
        if (eventDtoByInitiatorIdAndId.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        return EventMapper.toEvent(eventDtoByInitiatorIdAndId.get());
    }


    public Event updateEventInfo(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        UserDto userDto = checkForUser(userId);
        Event usersEventById = getUsersEventById(userId, eventId);
        if (usersEventById.getState().equals(EventState.PUBLISHED) ||
                usersEventById.getEventDate().isAfter(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        EventDto eventDto = EventMapper.toEventDto(updateEventUserRequest);

        Optional<StateAction> from = StateAction.from(updateEventUserRequest.getStateAction());
        if (from.isEmpty()) {
            throw new ConflictException(String.format("invalid state %s", updateEventUserRequest.getStateAction()));
        }
        if (from.get().equals(StateAction.CANCEL_REVIEW)) {
            eventDto.setState(EventState.CANCELED);
        } else {
            eventDto.setState(EventState.PUBLISHED);
        }

        eventDto.setId(eventId);
        eventDto.setCategory(CategoryMapper.toCategoryDto(usersEventById.getCategory()));
        eventDto.setInitiator(userDto);
        eventDto.setCreatedOn(usersEventById.getCreatedOn());
        eventDto.setConfirmedRequests(usersEventById.getConfirmedRequests());
        eventDto.setViews(usersEventById.getViews());
        return EventMapper.toEvent(eventRepository.save(eventDto));
    }

    public List<ParticipationRequest> getRequestToUsersEvent(Long userId, Long eventId) {
        checkForUser(userId);
        getUsersEventById(userId, eventId);
        return requestRepository.findParticipationRequestDtoByEventIdAndInitiatorId(eventId, userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateRequestsToUsersEvent(Long userId,
                                                                     Long eventId,
                                                                     EventRequestStatusUpdateRequest updateRequest) {
        checkForUser(userId);
        Event usersEventById = getUsersEventById(userId, eventId);
        List<ParticipationRequestDto> requests = requestRepository
                .findParticipationRequestDtosInRequestIds(updateRequest.getRequestIds());

        if (usersEventById.getParticipantLimit().equals(0) || usersEventById.getRequestModeration().equals(false)) {
            return new EventRequestStatusUpdateResult(requests
                    .stream()
                    .map(ParticipationRequestMapper::toParticipationRequest)
                    .collect(Collectors.toList()),
                    new ArrayList<>());
        }

        if (usersEventById.getParticipantLimit() >= requests.size()) {
            throw new ConflictException("The participant limit has been reached");
        }
        if (requests.stream().noneMatch(x -> x.getStatus().equals(EventState.PENDING))) {
            throw new ConflictException("Only pending requests can be updated");
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        for (ParticipationRequestDto request : requests) {
            if (!usersEventById.getParticipantLimit().equals(confirmedRequests.size())) {
                Optional<EventState> from = EventState.from(updateRequest.getStatus());
                if (from.isEmpty()) {
                    throw new ConflictException(String.format("invalid state %s", updateRequest.getStatus()));
                }
                request.setStatus(from.get());
                confirmedRequests.add(request);
            } else {
                break;
            }
        }
        requests.removeAll(confirmedRequests);
        return new EventRequestStatusUpdateResult(confirmedRequests.stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList()),
                requests.stream()
                .map(ParticipationRequestMapper::toParticipationRequest)
                .collect(Collectors.toList()));
    }
}
