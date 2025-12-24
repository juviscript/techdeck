package dev.juviscript.techdeck.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "time_entries")
@Data
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // MANY time entries belong to ONE work order.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    // MANY time entries belong to ONE technician.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType entryType;

    @Column(nullable = false)
    private LocalDateTime startTime;

    // Is nullable to allow open-ended time entries (e.g., ongoing work).
    private LocalDateTime endTime;

    // Calculated when endTime is set. Stored in minutes.
    private Integer durationMinutes;

    @Lob
    private String notes;

    // For offline sync tracking.
    private boolean isSynced = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}