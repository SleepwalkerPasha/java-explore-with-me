package ru.practicum.ewm.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.entities.CompilationDto;
import ru.practicum.ewm.dto.mapper.CompilationMapper;
import ru.practicum.ewm.dto.api.Compilation;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.pagination.PageRequester;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationService {

    private final CompilationRepository compilationRepository;

    @Transactional
    public List<Compilation> getAllCompilations(boolean pinned, int from, int size) {
        log.info("public: get compilations");
        return compilationRepository.getCompilationDtosByPinned(pinned, PageRequester.of(from, size))
                .stream()
                .map(CompilationMapper::toCompilation)
                .collect(Collectors.toList());
    }

    @Transactional
    public Compilation getCompilationById(long id) {
        Optional<CompilationDto> byId = compilationRepository.getCompilationDtoById(id);
        if (byId.isEmpty()) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", id));
        }
        log.info("public: get compilation by id {}", id);
        return CompilationMapper.toCompilation(byId.get());
    }
}
