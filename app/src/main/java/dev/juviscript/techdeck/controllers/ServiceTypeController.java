package dev.juviscript.techdeck.controllers;

import dev.juviscript.techdeck.dto.request.servicetype.CreateServiceTypeRequest;
import dev.juviscript.techdeck.dto.request.servicetype.UpdateServiceTypeRequest;
import dev.juviscript.techdeck.dto.response.ServiceTypeResponse;
import dev.juviscript.techdeck.mappers.ServiceTypeMapper;
import dev.juviscript.techdeck.models.ServiceType;
import dev.juviscript.techdeck.services.ServiceTypeService;
import dev.juviscript.techdeck.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/service-types")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;
    private final ServiceTypeMapper serviceTypeMapper;

    /**
     * GET /api/v1/service-types
     * Get all service types (optionally filter by active only)
     */
    @GetMapping
    public ResponseEntity<List<ServiceTypeResponse>> getAllServiceTypes(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        
        List<ServiceType> serviceTypes = activeOnly 
                ? serviceTypeService.getActiveServiceTypes()
                : serviceTypeService.getAllServiceTypes();
        
        List<ServiceTypeResponse> response = serviceTypes.stream()
                .map(serviceTypeMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/service-types/{id}
     * Get service type by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeResponse> getServiceTypeById(@PathVariable UUID id) {
        return serviceTypeService.getServiceTypeById(id)
                .map(serviceTypeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/service-types
     * Create a new service type
     */
    @PostMapping
    public ResponseEntity<ServiceTypeResponse> createServiceType(
            @Valid @RequestBody CreateServiceTypeRequest request) {
        
        ServiceType serviceType = serviceTypeMapper.toEntity(request);
        ServiceType savedServiceType = serviceTypeService.createServiceType(serviceType);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceTypeMapper.toResponse(savedServiceType));
    }

    /**
     * PUT /api/v1/service-types/{id}
     * Update an existing service type
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeResponse> updateServiceType(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceTypeRequest request) {

        return serviceTypeService.getServiceTypeById(id)
                .map(existingServiceType -> {
                    if (request.getName() != null) {
                        existingServiceType.setName(StringUtils.capitalizeWords(request.getName()));
                    }
                    if (request.getDescription() != null) {
                        existingServiceType.setDescription(StringUtils.trim(request.getDescription()));
                    }
                    if (request.getBaseRate() != null) {
                        existingServiceType.setBaseRate(request.getBaseRate());
                    }
                    if (request.getBaseDurationInMinutes() != null) {
                        existingServiceType.setBaseDurationInMinutes(request.getBaseDurationInMinutes());
                    }
                    if (request.getIsActive() != null) {
                        existingServiceType.setActive(request.getIsActive());
                    }

                    ServiceType updatedServiceType = serviceTypeService.updateServiceType(id, existingServiceType);
                    return ResponseEntity.ok(serviceTypeMapper.toResponse(updatedServiceType));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/service-types/{id}
     * Deactivate a service type (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateServiceType(@PathVariable UUID id) {
        if (serviceTypeService.getServiceTypeById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        serviceTypeService.deactivateServiceType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/service-types/{id}/activate
     * Reactivate a service type
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ServiceTypeResponse> activateServiceType(@PathVariable UUID id) {
        return serviceTypeService.getServiceTypeById(id)
                .map(serviceType -> {
                    serviceTypeService.activateServiceType(id);
                    serviceType.setActive(true);
                    return ResponseEntity.ok(serviceTypeMapper.toResponse(serviceType));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/service-types/check-name?name={name}
     * Check if name is available
     */
    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkNameAvailability(@RequestParam String name) {
        return ResponseEntity.ok(serviceTypeService.isNameAvailable(name));
    }
}
