package ru.practicum.ewm.closed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.ParticipationRequestDto;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequestDto, Long> {

    List<ParticipationRequestDto> findParticipationRequestDtosByRequester_Id(long requesterId);

    List<ParticipationRequestDto> findParticipationRequestDtosByEvent_Id(long eventId);

    Optional<ParticipationRequestDto> findParticipationRequestDtoByEvent_IdAndRequester_Id(long eventId, long requesterId);

    @Query("select pr from ParticipationRequestDto pr where pr.event.id = ?1 and pr.event.initiator.id = ?2")
    List<ParticipationRequestDto> findParticipationRequestDtoByEventIdAndInitiatorId(long eventId, long initiatorId);

    @Query("select pr from ParticipationRequestDto pr where pr.id in ?1")
    List<ParticipationRequestDto> findParticipationRequestDtosInRequestIds(List<Long> requestsIds);
}
