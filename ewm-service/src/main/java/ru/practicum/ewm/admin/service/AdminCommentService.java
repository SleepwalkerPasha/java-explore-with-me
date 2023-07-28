package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.closed.repository.CommentRepository;
import ru.practicum.ewm.dto.api.Comment;
import ru.practicum.ewm.dto.entities.CommentDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.mapper.CommentMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.StartAfterEndException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.EventRepository;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<Comment> getCommentsAdmin(Long eventId,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Long authorId,
                                          int from,
                                          int size) {
        Pageable pageable = PageRequester.of(from, size);
        return createCommentsRequest(eventId, rangeStart, rangeEnd, authorId, pageable)
                .stream()
                .map(CommentMapper::toComment)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId, Long eventId) {
        checkForEvent(eventId);
        CommentDto commentDto = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("comment with id %d not found", commentId)));
        return CommentMapper.toComment(commentDto);
    }

    @Transactional
    public void deleteComment(Long commentId, Long eventId) {
        EventDto eventDto = checkForEvent(eventId);
        CommentDto commentDto = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("comment with id %d not found", commentId)));
        if (!commentDto.getEvent().getId().equals(eventDto.getId())) {
            throw new ConflictException("event did not have this comment");
        }
        commentRepository.deleteById(commentId);
    }

    private EventDto checkForEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("event with id %d not found", eventId)));
    }

    private Page<CommentDto> createCommentsRequest(Long eventId,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Long authorId,
                                                   Pageable pageable) {
        return commentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (eventId != null) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("id"), eventId));
            }
            if (rangeEnd != null && rangeStart != null) {
                validateDateTime(rangeStart, rangeEnd);
                predicates.add(criteriaBuilder.between(root.get("created"), rangeStart, rangeEnd));
            }
            if (authorId != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("author").get("id"), authorId)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    private void validateDateTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new StartAfterEndException("invalid rangeEnd, rangeStart params: rangeStart is after rangeEnd");
        }
    }
}
