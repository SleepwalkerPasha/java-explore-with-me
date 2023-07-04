package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.api.Compilation;
import ru.practicum.ewm.dto.entities.CompilationDto;

import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(CompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        if (compilationDto.getId() != null) {
            compilation.setId(compilationDto.getId());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(compilationDto.getEvents()
                    .stream()
                    .map(EventMapper::toEventShort)
                    .collect(Collectors.toList()));
        }
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        if (compilation.getId() != null) {
            compilationDto.setId(compilation.getId());
        }
        if (compilation.getTitle() != null) {
            compilationDto.setTitle(compilation.getTitle());
        }
        if (compilation.getPinned() != null) {
            compilationDto.setPinned(compilation.getPinned());
        }
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(compilation.getEvents()
                    .stream()
                    .map(EventMapper::toEventDto)
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }
}
