package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.api.Compilation;
import ru.practicum.ewm.admin.dto.api.NewCompilation;
import ru.practicum.ewm.admin.dto.api.UpdateCompilationRequest;
import ru.practicum.ewm.dto.entities.CompilationDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.CompilationMapper;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminsCompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    public Compilation createCompilation(NewCompilation newCompilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setPinned(newCompilation.getPinned());
        compilationDto.setTitle(newCompilation.getTitle());

        List<EventDto> compilationEventDtos = eventRepository.getEventDtoByEventIds(newCompilation.getEvents());
        compilationDto.setEvents(compilationEventDtos);

        return CompilationMapper.toCompilation(compilationRepository.save(compilationDto));
    }

    public void deleteCompilation(long compId) {
        checkForCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    public Compilation updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        CompilationDto compilationDto = checkForCompilation(compId);

        if (updateCompilationRequest.getPinned() != null) {
            compilationDto.setPinned(updateCompilationRequest.getPinned());
        }
        Set<Long> events = updateCompilationRequest.getEvents();
        if (events != null) {
            List<EventDto> compilationEventDtos = eventRepository.getEventDtoByEventIds(events);
            compilationDto.setEvents(compilationEventDtos);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationDto.setTitle(updateCompilationRequest.getTitle());
        }

        return CompilationMapper.toCompilation(compilationRepository.save(compilationDto));
    }

    private CompilationDto checkForCompilation(long compId) {
        Optional<CompilationDto> compilationDtoOptional = compilationRepository.findById(compId);
        if (compilationDtoOptional.isEmpty()) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
        return compilationDtoOptional.get();
    }
}
