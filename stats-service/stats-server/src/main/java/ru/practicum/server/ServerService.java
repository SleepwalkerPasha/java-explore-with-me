package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointRequest;
import ru.practicum.server.dto.EndpointHit;
import ru.practicum.server.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;

    public EndpointHit saveEndpointRequest(EndpointRequest request) {
        EndpointHit endpointHit = EndpointMapper.toEndpointHit(request);
        return serverRepository.save(endpointHit);
    }

    public List<ViewStats> getViewStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid time interval");
        }

        if (uris == null || uris.isEmpty()) {
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
}
