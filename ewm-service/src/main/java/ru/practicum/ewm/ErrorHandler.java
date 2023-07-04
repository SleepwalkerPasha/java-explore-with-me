package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.dto.ApiError;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
}
