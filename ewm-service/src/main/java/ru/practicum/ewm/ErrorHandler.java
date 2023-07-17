package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.dto.api.ApiError;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.StartBeforeEndException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return new ApiError(e.getMessage(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now(),
                List.of(e.toString()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.error(e.getMessage());
        return new ApiError(e.getMessage(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now(),
                List.of(e.toString()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        log.error(e.getMessage());
        return new ApiError(e.getMessage(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                HttpStatus.FORBIDDEN.name(),
                LocalDateTime.now(),
                List.of(e.toString()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleStartBeforeEndException(final StartBeforeEndException e) {
        log.error(e.getMessage());
        return new ApiError(e.getMessage(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now(),
                List.of(e.toString()));
    }
}
