package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.dto.api.AdminStateAction;
import ru.practicum.ewm.closed.dto.api.UpdateEventRequest;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.dto.entities.CategoryDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.StartAfterEndException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminsEventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    public List<Event> getEventsByParams(String users,
                                         String states,
                                         String categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size) {
        List<Long> usersIds = new ArrayList<>();
        List<String> statesArray = new ArrayList<>();
        List<Long> categoriesIds = new ArrayList<>();
        if (users != null && !users.isBlank()) {
            usersIds = Arrays.stream(users.split(",")).map(Long::parseLong).collect(Collectors.toList());
        }
        if (states != null && !states.isBlank()) {
            statesArray = Arrays.stream(states.split(",")).collect(Collectors.toList());
        }
        if (categories != null && !categories.isBlank()) {
            categoriesIds = Arrays.stream(categories.split(",")).map(Long::parseLong).collect(Collectors.toList());
        }
        Pageable pageable = PageRequester.of(from, size);

        return creatingRequestAdmin(usersIds,
                statesArray,
                categoriesIds,
                rangeStart,
                rangeEnd,
                pageable)
                .stream()
                .map(EventMapper::toEvent)
                .collect(Collectors.toList());
    }

    @Transactional
    public Event updateEventAdmin(Long eventId, UpdateEventRequest updateEventAdminRequest) {
        Optional<EventDto> eventDtoOpt = eventRepository.findById(eventId);
        if (eventDtoOpt.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        EventDto oldEventDto = eventDtoOpt.get();
        if (Duration.between(oldEventDto.getEventDate(), LocalDateTime.now()).abs().toMinutes() < 60) {
            throw new ForbiddenException("Cannot publish the event because it's event date start is after now.plus(1H)");
        }
        EventDto eventDto = EventMapper.toEventDto(updateEventAdminRequest, oldEventDto);
        String stateAction = updateEventAdminRequest.getStateAction();
        if (stateAction != null) {
            Optional<AdminStateAction> from = AdminStateAction.from(stateAction);
            if (from.isEmpty()) {
                throw new ConflictException(String.format("invalid state %s", updateEventAdminRequest.getStateAction()));
            }
            if (from.get().equals(AdminStateAction.REJECT_EVENT)) {
                if (oldEventDto.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("event already published");
                } else if (oldEventDto.getState().equals(EventState.CANCELED)) {
                    throw new ConflictException("event already canceled");
                } else {
                    eventDto.setState(EventState.CANCELED);
                }
            } else {
                if (oldEventDto.getState().equals(EventState.PENDING)) {
                    eventDto.setState(EventState.PUBLISHED);
                    eventDto.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new ConflictException("event already published/canceled");
                }
            }
        } else {
            eventDto.setState(oldEventDto.getState());
        }
        eventDto.setId(eventId);
        Long catId = updateEventAdminRequest.getCategory();
        if (catId != null) {
            eventDto.setCategory(checkForCategory(catId));
        } else {
            eventDto.setCategory(oldEventDto.getCategory());
        }
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

    private Page<EventDto> creatingRequestAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {

        return eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty() && !(users.size() == 1 && users.get(0) == 0)) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("initiator"));
                for (Long initiatorId : users) {
                    in.value(initiatorId);
                }
                predicates.add(in);
            }

            if (states != null && !states.isEmpty()) {
                CriteriaBuilder.In<EventState> in = criteriaBuilder.in(root.get("state"));
                for (EventState state : states.stream().map(EventState::from)
                        .map(x -> x.orElseThrow(() -> new ConflictException("illegal state")))
                        .collect(Collectors.toList())) {
                    in.value(state);
                }
                predicates.add(in);
            }

            if (categories != null && !categories.isEmpty()) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("category"));
                for (Long categoriesId : categories) {
                    in.value(categoriesId);
                }
                predicates.add(in);
            }

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                    Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now)));
            if (rangeEnd != null) {
                validateDateTime(rangeStart, rangeEnd);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    private void validateDateTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new StartAfterEndException("invalid rangeEnd, rangeStart params: rangeStart is after rangeEnd");
        }
    }
}
