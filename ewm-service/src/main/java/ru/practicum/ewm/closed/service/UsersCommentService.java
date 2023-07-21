package ru.practicum.ewm.closed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.closed.repository.CommentRepository;
import ru.practicum.ewm.dto.api.Comment;
import ru.practicum.ewm.dto.api.EventState;
import ru.practicum.ewm.dto.api.NewComment;
import ru.practicum.ewm.dto.entities.CommentDto;
import ru.practicum.ewm.dto.entities.EventDto;
import ru.practicum.ewm.dto.entities.UserDto;
import ru.practicum.ewm.dto.mapper.CommentMapper;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.StartAfterEndException;
import ru.practicum.ewm.pagination.PageRequester;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersCommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Transactional
    public Comment addComment(long eventId, long userId, NewComment newComment) {
        CommentDto commentDto = new CommentDto();
        UserDto userDto = checkForUser(userId);
        EventDto eventDto = checkForEvent(eventId);
        commentDto.setText(newComment.getText());
        commentDto.setEvent(eventDto);
        commentDto.setAuthor(userDto);
        commentDto.setCreated(LocalDateTime.now());
        return CommentMapper.toComment(commentRepository.save(commentDto));
    }

    @Transactional(readOnly = true)
    public List<Comment> getComments(Long eventId,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Long userId,
                                     Long authorId,
                                     int from,
                                     int size) {
        Pageable pageable = PageRequester.of(from, size);
        checkForUser(userId);
        if (authorId != null) {
            checkForUser(authorId);
        }
        checkForEvent(eventId);
        return createCommentsRequest(eventId, rangeStart, rangeEnd, authorId, pageable)
                .stream()
                .map(CommentMapper::toComment)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId, Long eventId, Long userId) {
        checkForEvent(eventId);
        checkForUser(userId);
        CommentDto commentDto = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("comment with id %d not found", commentId)));
        return CommentMapper.toComment(commentDto);
    }

    @Transactional
    public void deleteComment(Long commentId, Long eventId, Long userId) {
        EventDto eventDto = checkForEvent(eventId);
        UserDto userDto = checkForUser(userId);
        CommentDto commentDto = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("comment with id %d not found", commentId)));
        if (!commentDto.getAuthor().getId().equals(userDto.getId())) {
            throw new ConflictException("only author can delete comment");
        }
        if (!commentDto.getEvent().getId().equals(eventDto.getId())) {
            throw new ConflictException("event did not have this comment");
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Comment updateComment(Long commentId, Long eventId, Long userId, NewComment newComment) {
        EventDto eventDto = checkForEvent(eventId);
        UserDto userDto = checkForUser(userId);
        CommentDto commentDto = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("comment with id %d not found", commentId)));
        if (!commentDto.getAuthor().getId().equals(userDto.getId())) {
            throw new ConflictException("only author can edit comment");
        }
        if (!commentDto.getEvent().getId().equals(eventDto.getId())) {
            throw new ConflictException("event did not have this comment");
        }
        commentDto.setText(newComment.getText());
        return CommentMapper.toComment(commentRepository.save(commentDto));
    }

    private UserDto checkForUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d not found", userId)));
    }

    private EventDto checkForEvent(long eventId) {
        EventDto eventDto = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("event with id %d not found", eventId)));
        if (eventDto.getState().equals(EventState.PENDING)) {
            throw new ConflictException("cannot add/get/delete comment to pending event");
        }
        return eventDto;
    }

    private Page<CommentDto> createCommentsRequest(long eventId,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Long authorId,
                                                   Pageable pageable) {
        return commentRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("event").get("id"), eventId));

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
