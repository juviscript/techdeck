package dev.juviscript.techdeck.dto.request.workorder;

import dev.juviscript.techdeck.models.OriginType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkOrderRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Service location ID is required")
    private UUID serviceLocationId;

    private UUID technicianId;

    @NotNull(message = "Scheduled date/time is required")
    private LocalDateTime scheduledDateTime;

    private Integer estimatedDurationMinutes;

    private String description;

    private OriginType originType;

    private UUID parentWorkOrderId;

    private List<CreateWorkOrderServiceRequest> services = new ArrayList<>();

    private List<CreateWorkOrderNoteRequest> notes = new ArrayList<>();
}
