package ru.practicum.ewm.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.StartAfterEndException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.EventRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private static final String EVENT_DATE = "EVENT_DATE";

    private static final String VIEWS = "VIEWS";

    private final EventRepository eventRepository;

    private final StatsClient hitsClient;

    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<EventShort> getEvents(String text,
                                      List<Long> categories,
                                      Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean onlyAvailable,
                                      String sort,
                                      int from,
                                      int size,
                                      String uri,
                                      String ip
    ) {
        Pageable pageable;
        if (sort != null && sort.equals(EVENT_DATE)) {
            pageable = PageRequester.of(from, size, Sort.by("eventDate"));
        } else if (sort != null && sort.equals(VIEWS)) {
            pageable = PageRequester.of(from, size, Sort.by("views"));
        } else {
            pageable = PageRequester.of(from, size);
        }
        LocalDateTime start = Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now);

        hitsClient.createHitRequest(new EndpointRequest("ewm-main-service",
                uri,
                ip,
                LocalDateTime.now()));

        List<EventShort> events = creatingRequestPublic(text,
                categories,
                paid,
                start,
                rangeEnd,
                onlyAvailable,
                pageable
        ).stream().map(EventMapper::toEventShort).collect(Collectors.toList());

        events.forEach(x -> x.setViews(x.getViews() + 1));

        return events;
    }

    @Transactional(readOnly = true)
    public Event getEventById(long id, String uri, String ip) {
        Optional<EventDto> byId = eventRepository.findById(id);
        if (byId.isEmpty() || !byId.get().getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", id));
        }
        hitsClient.createHitRequest(new EndpointRequest("ewm-main-service",
                uri,
                ip,
                LocalDateTime.now()));
        Event eventById = EventMapper.toEvent(byId.get());
        log.info("public: get event by id {}", id);

        ResponseEntity<Object> response = hitsClient
                .getStats(eventById.getCreatedOn(), LocalDateTime.now(), List.of(uri), true);
        List<ViewStats> stats = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        if (!stats.isEmpty()) {
            eventById.setViews(stats.get(0).getHits());
        }
        return eventById;
    }

    private void validateDateTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new StartAfterEndException("invalid rangeEnd, rangeStart params: rangeStart is after rangeEnd");
        }
    }


    private Page<EventDto> creatingRequestPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable) {
        return eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Predicate textOr1 = criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%" + text.toUpperCase() + "%");
            Predicate textOr2 = criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), "%" + text.toUpperCase() + "%");
            Predicate predicateForGrade = criteriaBuilder.or(textOr1, textOr2);
            predicates.add(predicateForGrade);

            if (categories != null && !categories.isEmpty()) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("category"));
                for (Long categoriesId : categories) {
                    in.value(categoriesId);
                }
                predicates.add(in);
            }

            if (paid != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("paid"), paid)));
            }
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                    rangeStart));
            if (rangeEnd != null) {
                validateDateTime(rangeStart, rangeEnd);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED)));

            if (onlyAvailable) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit"))));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
