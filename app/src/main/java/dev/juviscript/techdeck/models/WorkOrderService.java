package dev.juviscript.techdeck.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order_services")
@Data
public class WorkOrderService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // MANY work order services can belong to ONE work order.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    // MANY work order services can reference ONE service type.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Lob
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}