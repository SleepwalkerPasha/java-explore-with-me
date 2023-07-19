package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.closed.dto.api.NewEvent;
import ru.practicum.ewm.closed.dto.api.UpdateEventRequest;
import ru.practicum.ewm.dto.api.Event;
import ru.practicum.ewm.dto.api.EventShort;
import ru.practicum.ewm.dto.api.Location;
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
        if (eventDto.getId() != null) {
            event.setId(eventDto.getId());
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getConfirmedRequests() != null) {
            event.setConfirmedRequests(eventDto.getConfirmedRequests());
        }
        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(eventDto.getCategory()));
        }
        if (eventDto.getInitiator() != null) {
            event.setInitiator(UserMapper.toUserShort(eventDto.getInitiator()));
        }
        Location location = new Location();
        if (eventDto.getLocationLat() != null) {
            location.setLat(eventDto.getLocationLat());
        }
        if (eventDto.getLocationLon() != null) {
            location.setLon(eventDto.getLocationLon());
        }
        event.setLocation(location);
        if (eventDto.getCreatedOn() != null) {
            event.setCreatedOn(eventDto.getCreatedOn());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getPublishedOn() != null) {
            event.setPublishedOn(eventDto.getPublishedOn());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getState() != null) {
            event.setState(eventDto.getState());
        }
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

    public static EventDto toEventDto(NewEvent newEvent) {
        EventDto eventDto = new EventDto();
        if (newEvent.getAnnotation() != null) {
            eventDto.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getTitle() != null) {
            eventDto.setTitle(newEvent.getTitle());
        }
        if (newEvent.getPaid() != null) {
            eventDto.setPaid(newEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null) {
            eventDto.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getEventDate() != null) {
            eventDto.setEventDate(newEvent.getEventDate());
        }
        if (newEvent.getDescription() != null) {
            eventDto.setDescription(newEvent.getDescription());
        }
        if (newEvent.getRequestModeration() != null) {
            eventDto.setRequestModeration(newEvent.getRequestModeration());
        }
        if (newEvent.getLocation() != null) {
            if (newEvent.getLocation().getLat() != null) {
                eventDto.setLocationLat(newEvent.getLocation().getLat());
            }
            if (newEvent.getLocation().getLon() != null) {
                eventDto.setLocationLon(newEvent.getLocation().getLon());
            }
        }
        return eventDto;
    }

    public static EventDto toEventDto(UpdateEventRequest updateEventRequest, EventDto oldEvent) {
        EventDto eventDto = new EventDto();
        if (updateEventRequest.getAnnotation() != null) {
            eventDto.setAnnotation(updateEventRequest.getAnnotation());
        } else {
            eventDto.setAnnotation(oldEvent.getAnnotation());
        }
        if (updateEventRequest.getTitle() != null) {
            eventDto.setTitle(updateEventRequest.getTitle());
        } else {
            eventDto.setTitle(oldEvent.getTitle());
        }
        if (updateEventRequest.getPaid() != null) {
            eventDto.setPaid(updateEventRequest.getPaid());
        } else {
            eventDto.setPaid(oldEvent.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            eventDto.setParticipantLimit(updateEventRequest.getParticipantLimit());
        } else {
            eventDto.setParticipantLimit(oldEvent.getParticipantLimit());
        }
        if (updateEventRequest.getDescription() != null) {
            eventDto.setDescription(updateEventRequest.getDescription());
        } else {
            eventDto.setDescription(oldEvent.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            eventDto.setEventDate(updateEventRequest.getEventDate());
        } else {
            eventDto.setEventDate(oldEvent.getEventDate());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            eventDto.setRequestModeration(updateEventRequest.getRequestModeration());
        } else {
            eventDto.setRequestModeration(oldEvent.getRequestModeration());
        }
        if (updateEventRequest.getLocation() != null) {
            if (updateEventRequest.getLocation().getLat() != null) {
                eventDto.setLocationLat(updateEventRequest.getLocation().getLat());
            }
            if (updateEventRequest.getLocation().getLon() != null) {
                eventDto.setLocationLon(updateEventRequest.getLocation().getLon());
            }
        } else {
            eventDto.setLocationLat(oldEvent.getLocationLat());
            eventDto.setLocationLon(oldEvent.getLocationLon());
        }
        return eventDto;
    }

}
