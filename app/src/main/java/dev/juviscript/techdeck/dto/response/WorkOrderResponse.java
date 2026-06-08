package dev.juviscript.techdeck.dto.response;

import dev.juviscript.techdeck.models.OriginType;
import dev.juviscript.techdeck.models.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderResponse {

    private UUID id;
    private CustomerSummary customer;
    private ServiceLocationSummary serviceLocation;
    private TechnicianSummary technician;
    private Status status;
    private OriginType originType;
    private LocalDateTime scheduledDateTime;
    private Integer estimatedDurationMinutes;
    private String description;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;
    private UUID parentWorkOrderId;
    private List<WorkOrderServiceResponse> services;
    private List<WorkOrderNoteResponse> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested summary classes to avoid circular references
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceLocationSummary {
        private UUID id;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        private String accessNotes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnicianSummary {
        private UUID id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
    }
}
