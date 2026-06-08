package dev.juviscript.techdeck.controllers;

import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderNoteRequest;
import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderRequest;
import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderServiceRequest;
import dev.juviscript.techdeck.dto.request.workorder.UpdateWorkOrderRequest;
import dev.juviscript.techdeck.dto.response.WorkOrderNoteResponse;
import dev.juviscript.techdeck.dto.response.WorkOrderResponse;
import dev.juviscript.techdeck.dto.response.WorkOrderServiceResponse;
import dev.juviscript.techdeck.mappers.WorkOrderMapper;
import dev.juviscript.techdeck.models.ServiceLocation;
import dev.juviscript.techdeck.models.Status;
import dev.juviscript.techdeck.models.User;
import dev.juviscript.techdeck.models.WorkOrder;
import dev.juviscript.techdeck.repositories.ServiceLocationRepository;
import dev.juviscript.techdeck.repositories.UserRepository;
import dev.juviscript.techdeck.security.UserDetailsImpl;
import dev.juviscript.techdeck.services.WorkOrderService;
import dev.juviscript.techdeck.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderMapper workOrderMapper;
    private final UserRepository userRepository;
    private final ServiceLocationRepository serviceLocationRepository;

    // ==========================================
    // Work Order CRUD
    // ==========================================

    /**
     * GET /api/v1/work-orders
     * Get all work orders with optional filters
     */
    @GetMapping
    public ResponseEntity<List<WorkOrderResponse>> getAllWorkOrders(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID technicianId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Boolean unassigned) {

        List<WorkOrder> workOrders;

        if (Boolean.TRUE.equals(unassigned)) {
            workOrders = workOrderService.getUnassignedWorkOrders();
        } else if (technicianId != null && date != null) {
            workOrders = workOrderService.getByTechnicianAndDate(technicianId, date);
        } else if (technicianId != null && status != null) {
            workOrders = workOrderService.getByTechnicianAndStatus(technicianId, status);
        } else if (technicianId != null) {
            workOrders = workOrderService.getByTechnicianId(technicianId);
        } else if (customerId != null) {
            workOrders = workOrderService.getByCustomerId(customerId);
        } else if (status != null) {
            workOrders = workOrderService.getByStatus(status);
        } else if (date != null) {
            workOrders = workOrderService.getByScheduledDate(date);
        } else {
            workOrders = workOrderService.getAllWorkOrders();
        }

        List<WorkOrderResponse> response = workOrders.stream()
                .map(workOrderMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/work-orders/{id}
     * Get work order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(@PathVariable UUID id) {
        return workOrderService.getWorkOrderById(id)
                .map(workOrderMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/work-orders
     * Create a new work order
     */
    @PostMapping
    public ResponseEntity<WorkOrderResponse> createWorkOrder(
            @Valid @RequestBody CreateWorkOrderRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        try {
            WorkOrder workOrder = workOrderService.createWorkOrder(request, currentUser.getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(workOrderMapper.toResponse(workOrder));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/v1/work-orders/{id}
     * Update a work order
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> updateWorkOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkOrderRequest request) {

        return workOrderService.getWorkOrderById(id)
                .map(existingWorkOrder -> {
                    // Update technician if provided
                    if (request.getTechnicianId() != null) {
                        User technician = userRepository.findById(request.getTechnicianId())
                                .orElse(null);
                        existingWorkOrder.setAssignedTechnician(technician);
                    }
                    
                    // Update service location if provided
                    if (request.getServiceLocationId() != null) {
                        ServiceLocation location = serviceLocationRepository.findById(request.getServiceLocationId())
                                .orElse(null);
                        existingWorkOrder.setServiceLocation(location);
                    }
                    
                    if (request.getScheduledDateTime() != null) {
                        existingWorkOrder.setScheduledDateTime(request.getScheduledDateTime());
                    }
                    if (request.getEstimatedDurationMinutes() != null) {
                        existingWorkOrder.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
                    }
                    if (request.getDescription() != null) {
                        existingWorkOrder.setDescription(StringUtils.trim(request.getDescription()));
                    }
                    if (request.getStatus() != null) {
                        existingWorkOrder.setStatus(request.getStatus());
                    }
                    if (request.getJobStartTime() != null) {
                        existingWorkOrder.setJobStartTime(request.getJobStartTime());
                    }
                    if (request.getJobEndTime() != null) {
                        existingWorkOrder.setJobEndTime(request.getJobEndTime());
                    }

                    WorkOrder updated = workOrderService.updateWorkOrder(id, existingWorkOrder);
                    return ResponseEntity.ok(workOrderMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/v1/work-orders/{id}/status
     * Update work order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam Status status) {

        try {
            WorkOrder updated = workOrderService.updateStatus(id, status);
            return ResponseEntity.ok(workOrderMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /api/v1/work-orders/{id}/assign
     * Assign a technician to a work order
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<WorkOrderResponse> assignTechnician(
            @PathVariable UUID id,
            @RequestParam UUID technicianId) {

        try {
            WorkOrder updated = workOrderService.assignTechnician(id, technicianId);
            return ResponseEntity.ok(workOrderMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /api/v1/work-orders/{id}/unassign
     * Unassign technician from a work order
     */
    @PatchMapping("/{id}/unassign")
    public ResponseEntity<WorkOrderResponse> unassignTechnician(@PathVariable UUID id) {
        try {
            WorkOrder updated = workOrderService.unassignTechnician(id);
            return ResponseEntity.ok(workOrderMapper.toResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/v1/work-orders/{id}/cancel
     * Cancel a work order
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<WorkOrderResponse> cancelWorkOrder(@PathVariable UUID id) {
        try {
            WorkOrder canceled = workOrderService.cancelWorkOrder(id);
            return ResponseEntity.ok(workOrderMapper.toResponse(canceled));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/work-orders/{id}/follow-ups
     * Get follow-up work orders
     */
    @GetMapping("/{id}/follow-ups")
    public ResponseEntity<List<WorkOrderResponse>> getFollowUps(@PathVariable UUID id) {
        List<WorkOrderResponse> followUps = workOrderService.getFollowUps(id).stream()
                .map(workOrderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(followUps);
    }

    // ==========================================
    // Work Order Services
    // ==========================================

    /**
     * POST /api/v1/work-orders/{id}/services
     * Add a service to a work order
     */
    @PostMapping("/{id}/services")
    public ResponseEntity<WorkOrderServiceResponse> addService(
            @PathVariable UUID id,
            @Valid @RequestBody CreateWorkOrderServiceRequest request) {

        try {
            var service = workOrderService.addServiceToWorkOrder(id, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(workOrderMapper.toServiceResponse(service));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/work-orders/{workOrderId}/services/{serviceId}
     * Remove a service from a work order
     */
    @DeleteMapping("/{workOrderId}/services/{serviceId}")
    public ResponseEntity<Void> removeService(
            @PathVariable UUID workOrderId,
            @PathVariable UUID serviceId) {

        workOrderService.removeServiceFromWorkOrder(serviceId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // Work Order Notes
    // ==========================================

    /**
     * GET /api/v1/work-orders/{id}/notes
     * Get notes for a work order
     */
    @GetMapping("/{id}/notes")
    public ResponseEntity<List<WorkOrderNoteResponse>> getNotes(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "true") boolean includeInternal) {

        List<WorkOrderNoteResponse> notes = workOrderService.getNotes(id, includeInternal).stream()
                .map(workOrderMapper::toNoteResponse)
                .toList();
        return ResponseEntity.ok(notes);
    }

    /**
     * POST /api/v1/work-orders/{id}/notes
     * Add a note to a work order
     */
    @PostMapping("/{id}/notes")
    public ResponseEntity<WorkOrderNoteResponse> addNote(
            @PathVariable UUID id,
            @Valid @RequestBody CreateWorkOrderNoteRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        try {
            var note = workOrderService.addNoteToWorkOrder(id, request, currentUser.getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(workOrderMapper.toNoteResponse(note));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/work-orders/{workOrderId}/notes/{noteId}
     * Delete a note
     */
    @DeleteMapping("/{workOrderId}/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable UUID workOrderId,
            @PathVariable UUID noteId) {

        workOrderService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
