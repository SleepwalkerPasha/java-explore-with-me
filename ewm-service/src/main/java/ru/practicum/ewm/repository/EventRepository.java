package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.EventDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventDto, Long> {


    @Query("select e from EventDto e where " +
            "(lower(e.description) like lower(concat('%', ?1,'%')) " +
            "or lower(e.annotation) like lower(concat('%', ?1,'%'))) " +
            "and e.category.id in ?2 " +
            "and e.paid = ?3 " +
            "and e.participantLimit > 0 " +
            "and e.eventDate between ?4 and ?5")
    Page<EventDto> getEventDtoByParamsOnlyAvailable(String text, List<Long> categoriesId,
                                             boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from EventDto e where " +
            "(lower(e.description) like lower(concat('%', ?1,'%')) " +
            "or lower(e.annotation) like lower(concat('%', ?1,'%'))) " +
            "and e.category.id in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5")
    Page<EventDto> getEventDtoByParams(String text, List<Long> categoriesId,
                                                boolean paid, LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, Pageable pageable);
}
