package ru.practicum.ewm.dto.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.api.EventState;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    String annotation;

    String title;

    Long views;

    @Column(name = "confirmed_requests")
    Long confirmedRequests = 0L;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    Boolean paid = false;

    @JoinColumn(name = "category_id")
    @ManyToOne(targetEntity = CategoryDto.class, fetch = FetchType.LAZY)
    CategoryDto category;

    @JoinColumn(name = "initiator_id")
    @ManyToOne(targetEntity = UserDto.class, fetch = FetchType.LAZY)
    UserDto initiator;

    @Column(name = "location_lon")
    Double locationLon;

    @Column(name = "location_lat")
    Double locationLat;

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

    @Enumerated(EnumType.STRING)
    EventState state;

}
