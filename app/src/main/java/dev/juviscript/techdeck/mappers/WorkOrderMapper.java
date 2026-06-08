package dev.juviscript.techdeck.mappers;

import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderNoteRequest;
import dev.juviscript.techdeck.dto.request.workorder.CreateWorkOrderServiceRequest;
import dev.juviscript.techdeck.dto.response.WorkOrderNoteResponse;
import dev.juviscript.techdeck.dto.response.WorkOrderResponse;
import dev.juviscript.techdeck.dto.response.WorkOrderServiceResponse;
import dev.juviscript.techdeck.models.*;
import dev.juviscript.techdeck.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkOrderMapper {

    /**
     * Convert WorkOrder entity to WorkOrderResponse DTO
     */
    public WorkOrderResponse toResponse(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .customer(toCustomerSummary(workOrder.getCustomer()))
                .serviceLocation(toServiceLocationSummary(workOrder.getServiceLocation()))
                .technician(workOrder.getAssignedTechnician() != null
                        ? toTechnicianSummary(workOrder.getAssignedTechnician())
                        : null)
                .status(workOrder.getStatus())
                .originType(workOrder.getOriginType())
                .scheduledDateTime(workOrder.getScheduledDateTime())
                .estimatedDurationMinutes(workOrder.getEstimatedDurationMinutes())
                .description(workOrder.getDescription())
                .jobStartTime(workOrder.getJobStartTime())
                .jobEndTime(workOrder.getJobEndTime())
                .parentWorkOrderId(workOrder.getParentWorkOrder() != null 
                        ? workOrder.getParentWorkOrder().getId() 
                        : null)
                .services(toServiceResponses(workOrder.getWorkOrderServices()))
                .notes(toNoteResponses(workOrder.getWorkOrderNotes()))
                .createdAt(workOrder.getCreatedAt())
                .updatedAt(workOrder.getUpdatedAt())
                .build();
    }

    /**
     * Convert WorkOrderService entity to WorkOrderServiceResponse DTO
     */
    public WorkOrderServiceResponse toServiceResponse(WorkOrderService service) {
        return WorkOrderServiceResponse.builder()
                .id(service.getId())
                .serviceTypeId(service.getServiceType().getId())
                .serviceTypeName(service.getServiceType().getName())
                .baseRate(service.getServiceType().getBaseRate())
                .quantity(service.getQuantity())
                .notes(service.getNotes())
                .createdAt(service.getCreatedAt())
                .build();
    }

    /**
     * Convert WorkOrderNote entity to WorkOrderNoteResponse DTO
     */
    public WorkOrderNoteResponse toNoteResponse(WorkOrderNote note) {
        return WorkOrderNoteResponse.builder()
                .id(note.getId())
                .noteContent(note.getNoteContent())
                .isInternal(note.isInternal())
                .createdById(note.getCreatedBy().getId())
                .createdByName(note.getCreatedBy().getFirstName() + " " + note.getCreatedBy().getLastName())
                .createdAt(note.getCreatedAt())
                .build();
    }

    /**
     * Convert CreateWorkOrderServiceRequest to WorkOrderService entity
     * Note: ServiceType and WorkOrder must be set separately
     */
    public WorkOrderService toServiceEntity(CreateWorkOrderServiceRequest request) {
        WorkOrderService service = new WorkOrderService();
        service.setQuantity(request.getQuantity());
        service.setNotes(StringUtils.trim(request.getNotes()));
        return service;
    }

    /**
     * Convert CreateWorkOrderNoteRequest to WorkOrderNote entity
     * Note: CreatedBy and WorkOrder must be set separately
     */
    public WorkOrderNote toNoteEntity(CreateWorkOrderNoteRequest request) {
        WorkOrderNote note = new WorkOrderNote();
        note.setNoteContent(StringUtils.trim(request.getNoteContent()));
        note.setInternal(request.isInternal());
        return note;
    }

    // Helper methods for nested summaries

    private WorkOrderResponse.CustomerSummary toCustomerSummary(Customer customer) {
        return WorkOrderResponse.CustomerSummary.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }

    private WorkOrderResponse.ServiceLocationSummary toServiceLocationSummary(ServiceLocation location) {
        return WorkOrderResponse.ServiceLocationSummary.builder()
                .id(location.getId())
                .addressLine1(location.getAddressLine1())
                .addressLine2(location.getAddressLine2())
                .city(location.getCity())
                .state(location.getState())
                .zipCode(location.getZipCode())
                .accessNotes(location.getAccessNotes())
                .build();
    }

    private WorkOrderResponse.TechnicianSummary toTechnicianSummary(User technician) {
        return WorkOrderResponse.TechnicianSummary.builder()
                .id(technician.getId())
                .firstName(technician.getFirstName())
                .lastName(technician.getLastName())
                .phoneNumber(technician.getPhoneNumber())
                .build();
    }

    private List<WorkOrderServiceResponse> toServiceResponses(List<WorkOrderService> services) {
        if (services == null) return new ArrayList<>();
        return services.stream()
                .map(this::toServiceResponse)
                .toList();
    }

    private List<WorkOrderNoteResponse> toNoteResponses(List<WorkOrderNote> notes) {
        if (notes == null) return new ArrayList<>();
        return notes.stream()
                .map(this::toNoteResponse)
                .toList();
    }
}
