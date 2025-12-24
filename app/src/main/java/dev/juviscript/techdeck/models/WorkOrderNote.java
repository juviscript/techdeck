package dev.juviscript.techdeck.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order_notes")
@Data
public class WorkOrderNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // MANY notes belong to ONE work order.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    // MANY notes can be created by ONE user.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Lob
    @Column(nullable = false)
    private String noteContent;

    // True = Visible to only internal staff. False = Visible to customer as well.
    private boolean isInternal = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}