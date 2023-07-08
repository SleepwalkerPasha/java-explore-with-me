package ru.practicum.ewm.closed.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.dto.api.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEvent {

    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    Location location;

    Boolean paid = false;

    Integer participantLimit = 0;

    Boolean requestModeration = true;

    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}
