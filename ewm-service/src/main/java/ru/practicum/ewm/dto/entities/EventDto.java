package ru.practicum.ewm.dto.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.api.EventState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {

    @Id
    Long id;

    String annotation;

    String title;

    Long views;

    @Column(name = "confirmed_requests")
    Long confirmedRequests;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    Boolean paid;

    @JoinColumn(name = "category_id")
    @ManyToOne(targetEntity = CategoryDto.class, fetch = FetchType.LAZY)
    CategoryDto category;

    @JoinColumn(name = "initiator_id")
    @ManyToOne(targetEntity = CategoryDto.class, fetch = FetchType.LAZY)
    UserDto initiator;

    @JoinColumn(name = "location_id")
    @ManyToOne(targetEntity = LocationDto.class, fetch = FetchType.LAZY)
    LocationDto location;

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    List<CompilationDto> compilations;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    String description;

    @Column(name = "participant_limit")
    Integer participantLimit = 0;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration = true;

    EventState state;

}
