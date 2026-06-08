package dev.juviscript.techdeck.dto.request.workorder;

import dev.juviscript.techdeck.models.Status;
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
public class UpdateWorkOrderRequest {

    private UUID technicianId;

    private UUID serviceLocationId;

    private LocalDateTime scheduledDateTime;

    private Integer estimatedDurationMinutes;

    private String description;

    private Status status;

    private LocalDateTime jobStartTime;

    private LocalDateTime jobEndTime;
}
