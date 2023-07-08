package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.closed.dto.api.StateAction;
import ru.practicum.ewm.closed.dto.api.UpdateEventRequest;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminsEventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    public List<Event> getEventsByParams(List<Long> usersIds,
                                         List<String> states,
                                         List<Long> categoriesIds,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size) {
        Pageable pageable = PageRequester.of(from, size);
        log.info("admin: get events by params");
        return eventRepository.getEventDtoByParamsAdmin(usersIds,
                        states.stream().map(String::toLowerCase).collect(Collectors.toList()),
                        categoriesIds,
                        rangeStart,
                        rangeEnd,
                        pageable)
                .stream()
                .map(EventMapper::toEvent)
                .collect(Collectors.toList());
    }

    public Event updateEventAdmin(Long eventId, UpdateEventRequest updateEventAdminRequest) {
        Optional<EventDto> eventDtoOpt = eventRepository.findById(eventId);
        if (eventDtoOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        EventDto oldEventDto = eventDtoOpt.get();
        if (oldEventDto.getEventDate().isAfter(LocalDateTime.now().minusHours(1L))) {
            throw new ForbiddenException("Cannot publish the event because it's event date start is after now.minus(1H)");
        }
        EventDto eventDto = EventMapper.toEventDto(updateEventAdminRequest);
        Optional<StateAction> from = StateAction.from(updateEventAdminRequest.getStateAction());
        if (from.isEmpty()) {
            throw new ConflictException(String.format("invalid state %s", updateEventAdminRequest.getStateAction()));
        }
        if (from.get().equals(StateAction.CANCEL_REVIEW)) {
            if (!oldEventDto.getState().equals(EventState.PUBLISHED)) {
                eventDto.setState(EventState.CANCELED);
            }
        } else {
            if (oldEventDto.getState().equals(EventState.PENDING)) {
                eventDto.setState(EventState.PUBLISHED);
                eventDto.setPublishedOn(LocalDateTime.now());
            }
        }
        eventDto.setId(eventId);
        Long catId = updateEventAdminRequest.getCategory();
        eventDto.setCategory(checkForCategory(catId));
        eventDto.setInitiator(oldEventDto.getInitiator());
        eventDto.setCreatedOn(oldEventDto.getCreatedOn());
        eventDto.setConfirmedRequests(oldEventDto.getConfirmedRequests());
        eventDto.setViews(oldEventDto.getViews());
        log.info("admin: update event {}", eventId);
        return EventMapper.toEvent(eventRepository.save(eventDto));
    }

    private CategoryDto checkForCategory(long catId) {
        Optional<CategoryDto> categoryDtoOptional = categoryRepository.findById(catId);
        if (categoryDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }
        return categoryDtoOptional.get();
    }
}
