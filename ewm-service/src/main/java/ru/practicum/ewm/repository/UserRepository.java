package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.UserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDto, Long> {

    @Query("select u from UserDto u where u.id in ?1")
    Page<UserDto> findUserDtosByIds(List<Long> ids, Pageable pageable);
}
