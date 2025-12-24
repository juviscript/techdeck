package dev.juviscript.techdeck.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELED,
    WAITING_FOR_PARTS,
    NEEDS_FOLLOW_UP
}
