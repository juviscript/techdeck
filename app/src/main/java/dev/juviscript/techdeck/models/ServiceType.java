package dev.juviscript.techdeck.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_types")
@Data
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Lob
    private String description;

    @Column(nullable = false)
    private Integer baseDurationInMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRate;

    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}