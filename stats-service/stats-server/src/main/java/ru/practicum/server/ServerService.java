package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.EndpointRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.dto.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;

    @Transactional
    public EndpointHit saveEndpointRequest(EndpointRequest request) {
        EndpointHit endpointHit = EndpointMapper.toEndpointHit(request);
        return serverRepository.save(endpointHit);
    }

    @Transactional
    public List<ViewStats> getViewStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid time interval");
        }
        if (uris == null || uris.isEmpty() || (uris.size() == 1 && uris.get(0).equals(""))) {
            if (unique) {
                return serverRepository.findAllViewedEndpointsUnique(start, end);
            } else {
                return serverRepository.findAllViewedEndpoints(start, end);
            }
        } else {
            if (unique) {
                return serverRepository.findAllViewedEndpointsWithUrisUnique(start, end, uris);
            } else {
                return serverRepository.findAllViewedEndpointsWithUris(start, end, uris);
            }
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotFoundException(final IllegalArgumentException e) {
        log.error(e.getMessage());
        return Map.of(e.getMessage(), HttpStatus.BAD_REQUEST.name());
    }
}
