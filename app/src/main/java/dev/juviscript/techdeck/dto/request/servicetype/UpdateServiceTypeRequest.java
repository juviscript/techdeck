package dev.juviscript.techdeck.dto.request.servicetype;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceTypeRequest {

    private String name;

    private String description;

    @Min(value = 0, message = "Base rate must be positive")
    private BigDecimal baseRate;

    @Min(value = 1, message = "Base duration must be at least 1 minute")
    private Integer baseDurationInMinutes;

    private Boolean isActive;
}
