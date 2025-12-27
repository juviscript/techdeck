package dev.juviscript.techdeck.dto.request.servicetype;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceTypeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Base rate is required")
    @Min(value = 0, message = "Base rate must be positive")
    private BigDecimal baseRate;

    @NotNull(message = "Base duration is required")
    @Min(value = 1, message = "Base duration must be at least 1 minute")
    private Integer baseDurationInMinutes;
}
