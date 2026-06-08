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
public class WorkOrderNoteResponse {

    private UUID id;
    private String noteContent;
    private boolean isInternal;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
