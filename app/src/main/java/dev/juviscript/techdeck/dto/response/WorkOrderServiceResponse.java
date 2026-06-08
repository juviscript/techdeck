package dev.juviscript.techdeck.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderServiceResponse {

    private UUID id;
    private UUID serviceTypeId;
    private String serviceTypeName;
    private BigDecimal baseRate;
    private int quantity;
    private String notes;
    private LocalDateTime createdAt;
}
