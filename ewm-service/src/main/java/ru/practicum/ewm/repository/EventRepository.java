package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.EventDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<EventDto, Long>, JpaSpecificationExecutor<EventDto> {

    @Query("select e from EventDto e where e.id in ?1")
    List<EventDto> getEventDtoByEventIds(Set<Long> eventIds);

    @Query("select e from EventDto e where e.initiator.id = ?1")
    Page<EventDto> getEventDtoByInitiatorId(long initiatorId, Pageable pageable);

    @Query("select e from EventDto e where e.initiator.id = ?1 and e.id = ?2")
    Optional<EventDto> findEventDtoByInitiator_IdAndId(long initiatorId, long id);

    @Query("select e from EventDto e where e.category.id = ?1")
    List<EventDto> findEventsDtoByCategory(long catId);
}
