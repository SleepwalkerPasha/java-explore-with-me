package ru.practicum.ewm.dto.mapper;

import ru.practicum.ewm.dto.api.Comment;
import ru.practicum.ewm.dto.entities.CommentDto;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        if (commentDto.getId() != null) {
            comment.setId(commentDto.getId());
        }
        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
        }
        if (commentDto.getEvent() != null) {
            comment.setEvent(EventMapper.toEventShort(commentDto.getEvent()));
        }
        if (commentDto.getAuthor() != null) {
            comment.setAuthor(UserMapper.toUserShort(commentDto.getAuthor()));
        }
        if (commentDto.getCreated() != null) {
            comment.setCreated(commentDto.getCreated());
        }
        return comment;
    }
}
