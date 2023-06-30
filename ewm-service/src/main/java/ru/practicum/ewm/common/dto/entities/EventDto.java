package ru.practicum.ewm.common.dto.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

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

    Long confirmedRequests;

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
}
