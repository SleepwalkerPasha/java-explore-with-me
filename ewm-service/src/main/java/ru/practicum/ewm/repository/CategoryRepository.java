package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.CategoryDto;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryDto, Long> {

    @Query("select c from CategoryDto c")
    Page<CategoryDto> getCategoryDtos(Pageable pageable);

    @Query("select c from CategoryDto c where c.name = ?1")
    Optional<CategoryDto> getCategoryDtoByName(String name);

}
