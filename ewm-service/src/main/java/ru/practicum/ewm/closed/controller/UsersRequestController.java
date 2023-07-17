package ru.practicum.ewm.closed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.closed.service.UsersRequestService;
import ru.practicum.ewm.closed.dto.api.ParticipationRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UsersRequestController {

    private final UsersRequestService service;

    @GetMapping(path = "/{userId}/requests")
    public List<ParticipationRequest> getParticipationRequestsByUserId(@PathVariable long userId) {
        return service.getParticipationRequestsByUserId(userId);
    }

    @PostMapping(path = "/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequest createParticipationRequest(@PathVariable long userId,
                                                           @RequestParam(name = "eventId") long eventId) {
        return service.createParticipationRequest(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequest cancelParticipationRequest(@PathVariable long userId,
                                                           @PathVariable long requestId) {
        return service.cancelParticipationRequest(userId, requestId);
    }
}
