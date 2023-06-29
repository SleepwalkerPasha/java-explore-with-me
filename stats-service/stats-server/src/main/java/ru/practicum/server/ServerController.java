package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointRequest;
import ru.practicum.server.dto.EndpointHit;
import ru.practicum.server.dto.ViewStats;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final ServerService service;

    @PostMapping(path = "/hit")
    public EndpointHit saveRequest(@RequestBody @Valid EndpointRequest request) {
        return service.saveEndpointRequest(request);
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> getViewStatistics(@RequestParam(name = "start")
                                             @DateTimeFormat(
                                                     pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                             @RequestParam(name = "end")
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                             @RequestParam(name = "uris", required = false) List<String> uris,
                                             @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        return service.getViewStatistics(start, end, uris, unique);
    }
}
