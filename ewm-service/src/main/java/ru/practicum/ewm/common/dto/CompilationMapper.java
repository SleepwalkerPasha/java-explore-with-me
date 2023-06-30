package ru.practicum.ewm.common.dto;

import ru.practicum.ewm.common.dto.api.Compilation;
import ru.practicum.ewm.common.dto.entities.CompilationDto;

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
        return compilationDto;
    }
}
