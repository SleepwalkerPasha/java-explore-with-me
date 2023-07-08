package ru.practicum.ewm.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.common.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShort> getEvents(@RequestParam(name = "text") String text,
                                      @RequestParam(name = "categories") List<Long> categories,
                                      @RequestParam(name = "paid") Boolean paid,
                                      @RequestParam(name = "rangeStart")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(name = "rangeEnd")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(name = "onlyAvailable") Boolean onlyAvailable,
                                      @RequestParam(name = "sort") String sort,
                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping(path = "/{id}")
    public Event getEventById(@PathVariable(name = "id") long id) {
        return eventService.getEventById(id);
    }
}

