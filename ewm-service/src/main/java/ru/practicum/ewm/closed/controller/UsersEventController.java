package ru.practicum.ewm.closed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.closed.service.UsersEventService;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.closed.dto.api.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.closed.dto.api.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.closed.dto.api.NewEvent;
import ru.practicum.ewm.closed.dto.api.ParticipationRequest;
import ru.practicum.ewm.closed.dto.api.UpdateEventUserRequest;
import ru.practicum.ewm.exception.ForbiddenException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UsersEventController {

    private final UsersEventService usersEventService;

    @GetMapping(path = "/{userId}/events")
    public List<EventShort> getUsersAddedEvents(@PathVariable long userId,
                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        return usersEventService.getUsersAddedEvents(userId, from, size);
    }

    @PostMapping(path = "/{userId}/events")
    public Event addNewEvent(@PathVariable long userId, @RequestBody @Valid NewEvent newEvent) {
        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ForbiddenException(String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: %s", newEvent.getEventDate()));
        }
        return usersEventService.addNewEvent(userId, newEvent);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public Event getEventById(@PathVariable Long eventId, @PathVariable Long userId) {
        return usersEventService.getUsersEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public Event updateEventInfo(@PathVariable Long eventId, @PathVariable Long userId,
                                 @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return usersEventService.updateEventInfo(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequest> getRequestToUsersEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        return usersEventService.getRequestToUsersEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsToUsersEvent(@PathVariable Long eventId, @PathVariable Long userId,
                                                                     @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return usersEventService.updateRequestsToUsersEvent(userId, eventId, updateRequest);
    }
}