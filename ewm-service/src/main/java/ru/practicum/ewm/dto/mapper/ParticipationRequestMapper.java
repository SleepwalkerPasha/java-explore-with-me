package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.closed.dto.api.ParticipationRequest;
import ru.practicum.ewm.dto.entities.ParticipationRequestDto;

public class ParticipationRequestMapper {

    public static ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        if (participationRequestDto.getId() != null) {
            participationRequest.setId(participationRequestDto.getId());
        }
        if (participationRequestDto.getStatus() != null) {
            participationRequest.setStatus(participationRequestDto.getStatus());
        }
        if (participationRequestDto.getCreated() != null) {
            participationRequest.setCreated(participationRequestDto.getCreated());
        }
        if (participationRequestDto.getRequester() != null) {
            participationRequest.setRequester(participationRequestDto.getRequester().getId());
        }
        if (participationRequestDto.getEvent() != null) {
            participationRequest.setEvent(participationRequestDto.getEvent().getId());
        }
        return participationRequest;
    }

}
