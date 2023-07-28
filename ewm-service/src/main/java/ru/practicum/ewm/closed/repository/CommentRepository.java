package ru.practicum.ewm.closed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.entities.CommentDto;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentDto, Long>, JpaSpecificationExecutor<CommentDto> {

    @Query("select c from CommentDto c where c.event.id = ?1")
    List<CommentDto> findCommentsByEventId(long eventId);

}
