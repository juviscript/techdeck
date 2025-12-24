package dev.juviscript.techdeck.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
@Data
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // MANY work orders can belong to ONE customer.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // MANY work orders can be at ONE service location.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_location_id", nullable = false)
    private ServiceLocation serviceLocation;

    // MANY work orders can be assigned to ONE technician. Is nullable. If null, not yet assigned.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User assignedTechnician;

    // Self-Referencing: A work order can have a parent (e.g., Site Survey -> Installation).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_work_order_id")
    private WorkOrder parentWorkOrder;

    // Child work orders (inverse of parentWorkOrder above).
    @OneToMany(mappedBy = "parentWorkOrder")
    private List<WorkOrder> childWorkOrders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SCHEDULED;

    // ONE work order can have MANY notes.
    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderNote> workOrderNotes = new ArrayList<>();

    // ONE work order can have MANY services (through junction table)
    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderService> workOrderServices = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime scheduledDateTime;

    private Integer estimatedDurationMinutes;

    @Lob
    private String description;

    private LocalDateTime jobStartTime;

    private LocalDateTime jobEndTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}