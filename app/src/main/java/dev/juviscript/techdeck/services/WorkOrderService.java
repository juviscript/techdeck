package dev.juviscript.techdeck.services;

import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderNoteRequest;
import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderRequest;
import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderServiceRequest;
import dev.juviscript.techdeck.mappers.WorkOrderMapper;
import dev.juviscript.techdeck.models.*;
import dev.juviscript.techdeck.repositories.*;
import dev.juviscript.techdeck.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderNoteRepository workOrderNoteRepository;
    private final WorkOrderServiceRepository workOrderServiceRepository;
    private final CustomerRepository customerRepository;
    private final ServiceLocationRepository serviceLocationRepository;
    private final UserRepository userRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final WorkOrderMapper workOrderMapper;

    // ==========================================
    // Work Order CRUD
    // ==========================================

    /**
     * Get all work orders
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    /**
     * Get work order by ID
     */
    @Transactional(readOnly = true)
    public Optional<WorkOrder> getWorkOrderById(UUID id) {
        return workOrderRepository.findById(id);
    }

    /**
     * Create a new work order
     */
    public WorkOrder createWorkOrder(CreateWorkOrderRequest request, UUID createdByUserId) {
        // Validate and fetch customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        // Validate and fetch service location
        ServiceLocation serviceLocation = serviceLocationRepository.findById(request.getServiceLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Service location not found: " + request.getServiceLocationId()));

        // Validate service location belongs to customer
        if (!serviceLocation.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Service location does not belong to this customer");
        }

        // Fetch technician if provided
        User technician = null;
        if (request.getTechnicianId() != null) {
            technician = userRepository.findById(request.getTechnicianId())
                    .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + request.getTechnicianId()));
        }

        // Fetch parent work order if provided
        WorkOrder parentWorkOrder = null;
        if (request.getParentWorkOrderId() != null) {
            parentWorkOrder = workOrderRepository.findById(request.getParentWorkOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent work order not found: " + request.getParentWorkOrderId()));
        }

        // Create work order
        WorkOrder workOrder = new WorkOrder();
        workOrder.setCustomer(customer);
        workOrder.setServiceLocation(serviceLocation);
        workOrder.setAssignedTechnician(technician);
        workOrder.setScheduledDateTime(request.getScheduledDateTime());
        workOrder.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        workOrder.setDescription(StringUtils.trim(request.getDescription()));
        workOrder.setOriginType(request.getOriginType() != null ? request.getOriginType() : OriginType.DIRECT);
        workOrder.setStatus(Status.SCHEDULED);
        workOrder.setParentWorkOrder(parentWorkOrder);

        // Save work order first to get ID
        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        // Add services
        if (request.getServices() != null && !request.getServices().isEmpty()) {
            for (CreateWorkOrderServiceRequest serviceRequest : request.getServices()) {
                addServiceToWorkOrder(savedWorkOrder, serviceRequest);
            }
        }

        // Add notes
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            User createdBy = userRepository.findById(createdByUserId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + createdByUserId));
            
            for (CreateWorkOrderNoteRequest noteRequest : request.getNotes()) {
                addNoteToWorkOrder(savedWorkOrder, noteRequest, createdBy);
            }
        }

        return workOrderRepository.findById(savedWorkOrder.getId()).orElse(savedWorkOrder);
    }

    /**
     * Update work order
     */
    public WorkOrder updateWorkOrder(UUID id, WorkOrder updatedWorkOrder) {
        return workOrderRepository.findById(id)
                .map(workOrder -> {
                    if (updatedWorkOrder.getAssignedTechnician() != null) {
                        workOrder.setAssignedTechnician(updatedWorkOrder.getAssignedTechnician());
                    }
                    if (updatedWorkOrder.getServiceLocation() != null) {
                        workOrder.setServiceLocation(updatedWorkOrder.getServiceLocation());
                    }
                    if (updatedWorkOrder.getScheduledDateTime() != null) {
                        workOrder.setScheduledDateTime(updatedWorkOrder.getScheduledDateTime());
                    }
                    if (updatedWorkOrder.getEstimatedDurationMinutes() != null) {
                        workOrder.setEstimatedDurationMinutes(updatedWorkOrder.getEstimatedDurationMinutes());
                    }
                    if (updatedWorkOrder.getDescription() != null) {
                        workOrder.setDescription(updatedWorkOrder.getDescription());
                    }
                    if (updatedWorkOrder.getStatus() != null) {
                        workOrder.setStatus(updatedWorkOrder.getStatus());
                    }
                    if (updatedWorkOrder.getJobStartTime() != null) {
                        workOrder.setJobStartTime(updatedWorkOrder.getJobStartTime());
                    }
                    if (updatedWorkOrder.getJobEndTime() != null) {
                        workOrder.setJobEndTime(updatedWorkOrder.getJobEndTime());
                    }
                    return workOrderRepository.save(workOrder);
                })
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + id));
    }

    /**
     * Update work order status
     */
    public WorkOrder updateStatus(UUID id, Status newStatus) {
        return workOrderRepository.findById(id)
                .map(workOrder -> {
                    workOrder.setStatus(newStatus);
                    
                    // Auto-set timestamps based on status
                    if (newStatus == Status.IN_PROGRESS && workOrder.getJobStartTime() == null) {
                        workOrder.setJobStartTime(LocalDateTime.now());
                    } else if (newStatus == Status.COMPLETED && workOrder.getJobEndTime() == null) {
                        workOrder.setJobEndTime(LocalDateTime.now());
                    }
                    
                    return workOrderRepository.save(workOrder);
                })
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + id));
    }

    /**
     * Assign technician to work order
     */
    public WorkOrder assignTechnician(UUID workOrderId, UUID technicianId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianId));

        workOrder.setAssignedTechnician(technician);
        return workOrderRepository.save(workOrder);
    }

    /**
     * Unassign technician from work order
     */
    public WorkOrder unassignTechnician(UUID workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderId));

        workOrder.setAssignedTechnician(null);
        return workOrderRepository.save(workOrder);
    }

    /**
     * Cancel work order
     */
    public WorkOrder cancelWorkOrder(UUID id) {
        return updateStatus(id, Status.CANCELED);
    }

    // ==========================================
    // Query Methods
    // ==========================================

    /**
     * Get work orders by customer
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getByCustomerId(UUID customerId) {
        return workOrderRepository.findByCustomerId(customerId);
    }

    /**
     * Get work orders by technician
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getByTechnicianId(UUID technicianId) {
        return workOrderRepository.findByTechnicianId(technicianId);
    }

    /**
     * Get work orders by status
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getByStatus(Status status) {
        return workOrderRepository.findByStatus(status);
    }

    /**
     * Get work orders by technician and status
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getByTechnicianAndStatus(UUID technicianId, Status status) {
        return workOrderRepository.findByTechnicianIdAndStatus(technicianId, status);
    }

    /**
     * Get work orders scheduled for a specific date
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getByScheduledDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return workOrderRepository.findByScheduledDateTimeBetween(startOfDay, endOfDay);
    }

    /**
     * Get work orders for a technician on a specific date
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getByTechnicianAndDate(UUID technicianId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return workOrderRepository.findByTechnicianIdAndScheduledDateTimeBetween(technicianId, startOfDay, endOfDay);
    }

    /**
     * Get unassigned work orders
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getUnassignedWorkOrders() {
        return workOrderRepository.findByTechnicianIsNull();
    }

    /**
     * Get follow-up work orders
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getFollowUps(UUID parentWorkOrderId) {
        return workOrderRepository.findByParentWorkOrderId(parentWorkOrderId);
    }

    // ==========================================
    // Work Order Services
    // ==========================================

    /**
     * Add a service to a work order
     */
    public dev.juviscript.techdeck.models.WorkOrderService addServiceToWorkOrder(UUID workOrderId, CreateWorkOrderServiceRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderId));
        
        return addServiceToWorkOrder(workOrder, request);
    }

    private dev.juviscript.techdeck.models.WorkOrderService addServiceToWorkOrder(WorkOrder workOrder, CreateWorkOrderServiceRequest request) {
        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Service type not found: " + request.getServiceTypeId()));

        dev.juviscript.techdeck.models.WorkOrderService service = new dev.juviscript.techdeck.models.WorkOrderService();
        service.setWorkOrder(workOrder);
        service.setServiceType(serviceType);
        service.setQuantity(request.getQuantity());
        service.setNotes(StringUtils.trim(request.getNotes()));

        return workOrderServiceRepository.save(service);
    }

    /**
     * Remove a service from a work order
     */
    public void removeServiceFromWorkOrder(UUID serviceId) {
        workOrderServiceRepository.deleteById(serviceId);
    }

    // ==========================================
    // Work Order Notes
    // ==========================================

    /**
     * Add a note to a work order
     */
    public WorkOrderNote addNoteToWorkOrder(UUID workOrderId, CreateWorkOrderNoteRequest request, UUID createdByUserId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderId));
        
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + createdByUserId));

        return addNoteToWorkOrder(workOrder, request, createdBy);
    }

    private WorkOrderNote addNoteToWorkOrder(WorkOrder workOrder, CreateWorkOrderNoteRequest request, User createdBy) {
        WorkOrderNote note = new WorkOrderNote();
        note.setWorkOrder(workOrder);
        note.setNoteContent(StringUtils.trim(request.getNoteContent()));
        note.setInternal(request.isInternal());
        note.setCreatedBy(createdBy);

        return workOrderNoteRepository.save(note);
    }

    /**
     * Get notes for a work order (optionally filter internal)
     */
    @Transactional(readOnly = true)
    public List<WorkOrderNote> getNotes(UUID workOrderId, boolean includeInternal) {
        if (includeInternal) {
            return workOrderNoteRepository.findByWorkOrderId(workOrderId);
        }
        return workOrderNoteRepository.findByWorkOrderIdAndIsInternalFalse(workOrderId);
    }

    /**
     * Delete a note
     */
    public void deleteNote(UUID noteId) {
        workOrderNoteRepository.deleteById(noteId);
    }
}
