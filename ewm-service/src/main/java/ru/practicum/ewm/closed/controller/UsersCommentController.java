package ru.practicum.ewm.closed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.closed.service.UsersCommentService;
import ru.practicum.ewm.dto.api.Comment;
import ru.practicum.ewm.dto.api.NewComment;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersCommentController {

    private final UsersCommentService usersCommentService;

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addCommentToEvent(@PathVariable Long eventId,
                                     @PathVariable Long userId,
                                     @RequestBody @Valid NewComment newComment) {
        return usersCommentService.addComment(eventId, userId, newComment);
    }

    @GetMapping("/{userId}/events/{eventId}/comments")
    public List<Comment> getComments(@PathVariable Long eventId,
                                     @PathVariable Long userId,
                                     @RequestParam(name = "rangeStart", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(name = "rangeEnd", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(name = "authorId", required = false) Long authorId,
                                     @RequestParam(required = false, defaultValue = "0") int from,
                                     @RequestParam(required = false, defaultValue = "10") int size) {
        return usersCommentService.getComments(eventId, rangeStart, rangeEnd, userId, authorId, from, size);
    }

    @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long eventId, @PathVariable Long userId) {
        usersCommentService.deleteComment(commentId, eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public Comment getCommentById(@PathVariable Long commentId, @PathVariable Long userId, @PathVariable Long eventId) {
        return usersCommentService.getCommentById(commentId, eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public Comment updateComment(@PathVariable Long commentId, @PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid NewComment newComment) {
        return usersCommentService.updateComment(commentId, eventId, userId, newComment);
    }
}
