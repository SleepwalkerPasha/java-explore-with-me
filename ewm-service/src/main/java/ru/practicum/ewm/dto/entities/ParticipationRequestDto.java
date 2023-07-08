package ru.practicum.ewm.dto.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.api.EventState;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participations")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserDto.class)
    @JoinColumn(name = "requester_id")
    UserDto requester;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = EventDto.class)
    @JoinColumn(name = "event_id")
    EventDto event;

    @Enumerated(value = EnumType.STRING)
    EventState status;

    LocalDateTime created;

}
