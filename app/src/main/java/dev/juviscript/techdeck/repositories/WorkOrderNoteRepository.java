package dev.juviscript.techdeck.repositories;

import dev.juviscript.techdeck.models.WorkOrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderNoteRepository extends JpaRepository<WorkOrderNote, UUID> {

    List<WorkOrderNote> findByWorkOrderId(UUID workOrderId);

    List<WorkOrderNote> findByWorkOrderIdAndIsInternalFalse(UUID workOrderId);

    List<WorkOrderNote> findByCreatedById(UUID userId);
}
