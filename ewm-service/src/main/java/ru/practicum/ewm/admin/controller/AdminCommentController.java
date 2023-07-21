package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.admin.service.AdminCommentService;
import ru.practicum.ewm.dto.api.Comment;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final AdminCommentService commentService;

    @GetMapping
    public List<Comment> getComments(@RequestParam(name = "eventId", required = false) Long eventId,
                                     @RequestParam(name = "authorId", required = false) Long authorId,
                                     @RequestParam(name = "rangeStart", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(name = "rangeEnd", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return commentService.getCommentsAdmin(eventId, rangeStart, rangeEnd, authorId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @RequestParam(name = "eventId") Long eventId) {
        commentService.deleteComment(commentId, eventId);
    }

    @GetMapping("/{commentId}")
    public Comment getCommentById(@PathVariable Long commentId, @RequestParam(name = "eventId") Long eventId) {
        return commentService.getCommentById(commentId, eventId);
    }
}
