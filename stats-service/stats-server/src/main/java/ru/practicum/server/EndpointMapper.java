package ru.practicum.server;

import ru.practicum.dto.EndpointRequest;
import ru.practicum.server.dto.EndpointHit;

public class EndpointMapper {

    public static EndpointHit toEndpointHit(EndpointRequest endpointRequest) {
        EndpointHit endpointHit = new EndpointHit();
        if (endpointRequest.getIp() != null) {
            endpointHit.setIp(endpointRequest.getIp());
        }
        if (endpointRequest.getApp() != null) {
            endpointHit.setApp(endpointRequest.getApp());
        }
        if (endpointRequest.getUri() != null) {
            endpointHit.setUri(endpointRequest.getUri());
        }
        if (endpointRequest.getTimestamp() != null) {
            endpointHit.setTimestamp(endpointRequest.getTimestamp());
        }
        return endpointHit;
    }

    public static EndpointRequest toEndpointRequest(EndpointHit endpointHit) {
        EndpointRequest endpointRequest = new EndpointRequest();
        if (endpointHit.getApp() != null) {
            endpointRequest.setApp(endpointHit.getApp());
        }
        if (endpointHit.getUri() != null) {
            endpointRequest.setUri(endpointHit.getUri());
        }
        if (endpointHit.getTimestamp() != null) {
            endpointRequest.setTimestamp(endpointHit.getTimestamp());
        }
        if (endpointHit.getIp() != null) {
            endpointRequest.setIp(endpointHit.getIp());
        }
        return endpointRequest;
    }
}
