package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointRequest;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.dto.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

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
        if (uris == null || uris.isEmpty() || (uris.size() == 1 && uris.get(0).isBlank())) {
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
