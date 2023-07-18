package ru.practicum.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.dto.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ServerRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(h.ip) as hits) from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 group by h.uri, h.app order by hits desc ")
    List<ViewStats> findAllViewedEndpoints(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(h.ip) as hits) from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 and h.uri in (?3) group by h.uri, h.app order by hits desc ")
    List<ViewStats> findAllViewedEndpointsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(distinct h.ip) as hits) from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 group by h.uri, h.app order by hits desc ")
    List<ViewStats> findAllViewedEndpointsUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStats(h.app, h.uri, count(distinct h.ip) as hits) from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 and h.uri in (?3) group by h.uri, h.app order by hits desc ")
    List<ViewStats> findAllViewedEndpointsWithUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
