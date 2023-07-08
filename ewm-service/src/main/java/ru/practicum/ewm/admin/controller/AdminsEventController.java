package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.admin.service.AdminsEventService;
import ru.practicum.ewm.closed.dto.api.UpdateEventRequest;
import ru.practicum.ewm.dto.api.Event;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminsEventController {

    private final AdminsEventService service;

    @GetMapping(path = "")
    public List<Event> getEventsByParams(@RequestParam(name = "users") List<Long> users,
                                         @RequestParam(name = "states") List<String> states,
                                         @RequestParam(name = "categories") List<Long> categories,
                                         @RequestParam(name = "rangeStart")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd")
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.getEventsByParams(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/{eventId}")
    public Event updateEventAdmin(@PathVariable Long eventId, @RequestBody @Valid UpdateEventRequest updateEventAdminRequest) {
        return service.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
