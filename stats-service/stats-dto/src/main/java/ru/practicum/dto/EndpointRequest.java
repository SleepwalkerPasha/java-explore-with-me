package ru.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EndpointRequest {

    @NotEmpty
    String app;

    @NotEmpty
    String uri;

    @NotEmpty
    String ip;

    @NotEmpty
    String timestamp;
}
