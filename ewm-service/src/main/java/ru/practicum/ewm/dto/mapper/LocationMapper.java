package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.api.Location;
import ru.practicum.ewm.dto.entities.LocationDto;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        Location location = new Location();
        if (locationDto.getId() != null) {
            location.setId(locationDto.getId());
        }
        if (locationDto.getLat() != null) {
            location.setLat(locationDto.getLat());
        }
        if (locationDto.getLon() != null) {
            location.setLon(locationDto.getLon());
        }
        return location;
    }

    public static LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        if (location.getId() != null) {
            locationDto.setId(location.getId());
        }
        if (location.getLat() != null) {
            locationDto.setLat(location.getLat());
        }
        if (location.getLon() != null) {
            locationDto.setLon(location.getLon());
        }
        return locationDto;
    }
}
