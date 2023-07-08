package ru.practicum.ewm.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.EventMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final static String EVENT_DATE = "EVENT_DATE";

    private final static String VIEWS = "VIEWS";

    private final EventRepository eventRepository;

    public List<EventShort> getEvents(String text,
                                      List<Long> categories,
                                      Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean onlyAvailable,
                                      String sort,
                                      int from,
                                      int size) {
        Pageable pageable;
        if (sort.equals(EVENT_DATE)) {
            pageable = PageRequester.of(from, size, Sort.by("eventDate"));
        } else if (sort.equals(VIEWS)) {
            pageable = PageRequester.of(from, size, Sort.by("views"));
        } else {
            pageable = PageRequester.of(from, size);
        }
        Page<EventDto> eventDtoPage;
        if (onlyAvailable) {
            eventDtoPage = eventRepository.getEventDtoByParamsOnlyAvailable(text, categories, paid, rangeStart, rangeEnd, pageable);
        } else {
            eventDtoPage = eventRepository.getEventDtoByParams(text, categories, paid, rangeStart, rangeEnd, pageable);
        }
        return eventDtoPage.stream().map(EventMapper::toEventShort).collect(Collectors.toList());
    }

    public Event getEventById(long id) {
        Optional<EventDto> byId = eventRepository.findById(id);
        if (byId.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", id));
        }
        return EventMapper.toEvent(byId.get());
    }
}
