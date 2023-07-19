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
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminsEventController {

    private final AdminsEventService service;

    @GetMapping
    public List<Event> getEventsByParams(@RequestParam(name = "users", required = false) @Valid @NotBlank String users,
                                         @RequestParam(name = "states", required = false) @Valid @NotBlank String states,
                                         @RequestParam(name = "categories", required = false) @Valid @NotBlank String categories,
                                         @RequestParam(name = "rangeStart", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                         @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return service.getEventsByParams(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
    }

    @PatchMapping(path = "/{eventId}")
    public Event updateEventAdmin(@PathVariable Long eventId, @RequestBody @Valid UpdateEventRequest updateEventAdminRequest) {
        return service.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
