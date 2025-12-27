package dev.juviscript.techdeck.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLocationResponse {

    private UUID id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String accessNotes;
    private boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}