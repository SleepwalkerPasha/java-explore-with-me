package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.dto.EndpointHit;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServerController {

    private final ServerService service;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit saveRequest(@RequestBody @Valid EndpointRequest request) {
        log.info("receive save request {}", request);
        return service.saveEndpointRequest(request);
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> getViewStatistics(@RequestParam(name = "start")
                                             @DateTimeFormat(
                                                     pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                             @RequestParam(name = "end")
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                             @RequestParam(name = "uris", required = false, defaultValue = "") String uris,
                                             @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        List<ViewStats> stats = service.getViewStatistics(start, end, Arrays.stream(uris.split(",")).collect(Collectors.toList()), unique);
        log.info("receive stats requests {}", stats);
        return stats;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final IllegalArgumentException e) {
        log.error(e.getMessage());
        return Map.of(e.getMessage(), HttpStatus.BAD_REQUEST.name());
    }
}
