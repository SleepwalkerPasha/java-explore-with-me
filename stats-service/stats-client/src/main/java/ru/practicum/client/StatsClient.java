package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatsClient {

    @Value("${stats-server.url}")
    private String serverUrl;
    private final RestTemplate rest;

    public StatsClient() {
        this.rest = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        rest.setRequestFactory(requestFactory);
    }

    public ResponseEntity<Object> createHitRequest(EndpointRequest hit) {

        ResponseEntity<Object> response;

        try {
            response = rest.postForEntity(serverUrl + "/hit", hit, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        log.info("send request {}", hit);
        return responseBuilder.build();
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        StringBuilder url = new StringBuilder(serverUrl + "/stats?");
        url.append("&uris=").append(String.join(",", uris));
        url.append("&unique=").append(unique);
        url.append("&start=").append(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        url.append("&end=").append(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        ResponseEntity<Object> response;

        try {
            response = rest.exchange(url.toString(), HttpMethod.GET, null, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            log.info("get response {}", responseBuilder.body(response.getBody()));
            return responseBuilder.body(response.getBody());
        }
        log.info("response with no body {}", responseBuilder.build());
        return responseBuilder.build();
    }
}