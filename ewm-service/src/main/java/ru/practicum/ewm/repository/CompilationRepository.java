package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.CompilationDto;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<CompilationDto, Long> {

    @Query("select cd from CompilationDto cd where cd.pinned = ?1")
    Page<CompilationDto> getCompilationDtosByPinned(boolean pinned, Pageable pageable);

    Optional<CompilationDto> getCompilationDtoById(long id);
}
