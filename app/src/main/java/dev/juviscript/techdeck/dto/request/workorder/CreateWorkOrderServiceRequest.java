package dev.juviscript.techdeck.dto.request.workorder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkOrderServiceRequest {

    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    private String notes;
}
