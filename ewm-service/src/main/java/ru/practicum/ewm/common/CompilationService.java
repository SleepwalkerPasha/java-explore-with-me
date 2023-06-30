package ru.practicum.ewm.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.dto.CompilationMapper;
import ru.practicum.ewm.common.dto.api.Compilation;
import ru.practicum.ewm.pagination.PageRequester;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;

    public List<Compilation> getAllCompilations(boolean pinned, int from, int size) {
        return compilationRepository.getCompilationDtosByPinned(pinned, PageRequester.of(from, size))
                .stream()
                .map(CompilationMapper::toCompilation)
                .collect(Collectors.toList());
    }

    public Compilation getCompilationById(long id) {
        return CompilationMapper.toCompilation(compilationRepository.getCompilationDtoById(id));
    }
}
