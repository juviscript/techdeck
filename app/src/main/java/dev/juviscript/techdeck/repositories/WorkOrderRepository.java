package dev.juviscript.techdeck.repositories;

import dev.juviscript.techdeck.models.Status;
import dev.juviscript.techdeck.models.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    // Find by customer
    List<WorkOrder> findByCustomerId(UUID customerId);

    // Find by technician
    List<WorkOrder> findByTechnicianId(UUID technicianId);

    // Find by status
    List<WorkOrder> findByStatus(Status status);

    // Find by technician and status
    List<WorkOrder> findByTechnicianIdAndStatus(UUID technicianId, Status status);

    // Find by parent work order (follow-ups)
    List<WorkOrder> findByParentWorkOrderId(UUID parentWorkOrderId);

    // Find by scheduled date range
    @Query("SELECT w FROM WorkOrder w WHERE w.scheduledDateTime BETWEEN :start AND :end")
    List<WorkOrder> findByScheduledDateTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Find by technician and scheduled date range
    @Query("SELECT w FROM WorkOrder w WHERE w.technician.id = :technicianId " +
           "AND w.scheduledDateTime BETWEEN :start AND :end")
    List<WorkOrder> findByTechnicianIdAndScheduledDateTimeBetween(
            @Param("technicianId") UUID technicianId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Find unassigned work orders
    List<WorkOrder> findByTechnicianIsNull();

    // Find work orders by service location
    List<WorkOrder> findByServiceLocationId(UUID serviceLocationId);

    // Count by status
    long countByStatus(Status status);

    // Count by technician and status
    long countByTechnicianIdAndStatus(UUID technicianId, Status status);
}
