package ru.practicum.ewm.dto.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.closed.dto.api.RequestState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participations")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserDto.class)
    @JoinColumn(name = "requester_id")
    UserDto requester;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = EventDto.class)
    @JoinColumn(name = "event_id")
    EventDto event;

    @Enumerated(value = EnumType.STRING)
    RequestState status;

    LocalDateTime created;

}
