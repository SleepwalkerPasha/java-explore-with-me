package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.dto.entities.EventDto;

public class EventMapper {

    public static EventShort toEventShort(EventDto eventDto) {
        EventShort eventShort = new EventShort();
        if (eventDto.getId() != null) {
            eventShort.setId(eventDto.getId());
        }
        if (eventDto.getAnnotation() != null) {
            eventShort.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getTitle() != null) {
            eventShort.setTitle(eventDto.getTitle());
        }
        if (eventDto.getViews() != null) {
            eventShort.setViews(eventDto.getViews());
        }
        if (eventDto.getConfirmedRequests() != null) {
            eventShort.setConfirmedRequests(eventDto.getConfirmedRequests());
        }
        if (eventDto.getEventDate() != null) {
            eventShort.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getPaid() != null) {
            eventShort.setPaid(eventDto.getPaid());
        }
        if (eventDto.getCategory() != null) {
            eventShort.setCategory(CategoryMapper.toCategory(eventDto.getCategory()));
        }
        if (eventDto.getInitiator() != null) {
            eventShort.setInitiator(UserMapper.toUserShort(eventDto.getInitiator()));
        }
        return eventShort;
    }

    public static Event toEvent(EventDto eventDto) {
        Event event = new Event();

        return event;
    }


    public static EventDto toEventDto(EventShort eventShort) {
        EventDto eventDto = new EventDto();
        if (eventShort.getId() != null) {
            eventDto.setId(eventShort.getId());
        }
        if (eventShort.getAnnotation() != null) {
            eventDto.setAnnotation(eventShort.getAnnotation());
        }
        if (eventShort.getTitle() != null) {
            eventDto.setTitle(eventShort.getTitle());
        }
        if (eventShort.getViews() != null) {
            eventDto.setViews(eventShort.getViews());
        }
        if (eventShort.getConfirmedRequests() != null) {
            eventDto.setConfirmedRequests(eventShort.getConfirmedRequests());
        }
        if (eventShort.getEventDate() != null) {
            eventDto.setEventDate(eventShort.getEventDate());
        }
        if (eventShort.getPaid() != null) {
            eventDto.setPaid(eventShort.getPaid());
        }
        if (eventShort.getCategory() != null) {
            eventDto.setCategory(CategoryMapper.toCategoryDto(eventShort.getCategory()));
        }
        if (eventShort.getInitiator() != null) {
            eventDto.setInitiator(UserMapper.toUserDto(eventShort.getInitiator()));
        }
        return eventDto;
    }
}
