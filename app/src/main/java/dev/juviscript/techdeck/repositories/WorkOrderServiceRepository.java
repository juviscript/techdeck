package dev.juviscript.techdeck.repositories;

import dev.juviscript.techdeck.models.WorkOrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderServiceRepository extends JpaRepository<WorkOrderService, UUID> {

    List<WorkOrderService> findByWorkOrderId(UUID workOrderId);

    List<WorkOrderService> findByServiceTypeId(UUID serviceTypeId);

    void deleteByWorkOrderId(UUID workOrderId);
}
