package ru.practicum.ewm.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsClient;
import ru.practicum.ewm.common.service.EventService;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@ComponentScan(basePackageClasses = StatsClient.class)
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShort> getEvents(@RequestParam(name = "text", required = false, defaultValue = "") String text,
                                      @RequestParam(name = "categories", required = false, defaultValue = "") String categories,
                                      @RequestParam(name = "paid", required = false) Boolean paid,
                                      @RequestParam(name = "rangeStart", required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(name = "rangeEnd", required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(name = "sort", required = false) String sort,
                                      @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                      HttpServletRequest request) {
        return eventService.getEvents(text,
                Arrays.stream(categories.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList()), paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request.getRequestURI(),
                request.getRemoteAddr()
        );
    }

    @GetMapping(path = "/{id}")
    public Event getEventById(@PathVariable(name = "id") long id, HttpServletRequest request) {
        return eventService.getEventById(id, request.getRequestURI(), request.getRemoteAddr());
    }
}

