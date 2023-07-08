package ru.practicum.ewm.closed.dto.api;

import java.util.Optional;

public enum StateAction {
    SEND_TO_REVIEW, CANCEL_REVIEW;

    public static Optional<StateAction> from(String stringState) {
        for (StateAction state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
