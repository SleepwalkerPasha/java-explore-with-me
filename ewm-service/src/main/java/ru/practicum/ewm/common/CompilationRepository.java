package ru.practicum.ewm.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.common.dto.entities.CompilationDto;

public interface CompilationRepository extends JpaRepository<CompilationDto, Long> {

    @Query("select cd from CompilationDto cd where cd.pinned = ?1")
    Page<CompilationDto> getCompilationDtosByPinned(boolean pinned, Pageable pageable);

    CompilationDto getCompilationDtoById(long id);
}
