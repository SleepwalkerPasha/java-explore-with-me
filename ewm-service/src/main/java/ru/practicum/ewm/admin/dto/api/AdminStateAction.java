package ru.practicum.ewm.admin.dto.api;

import java.util.Optional;

public enum AdminStateAction {
    PUBLISH_EVENT, REJECT_EVENT;

    public static Optional<AdminStateAction> from(String stringState) {
        for (AdminStateAction state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
