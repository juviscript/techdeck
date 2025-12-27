package dev.juviscript.techdeck.services;

import dev.juviscript.techdeck.models.ServiceType;
import dev.juviscript.techdeck.repositories.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * Get all service types
     */
    @Transactional(readOnly = true)
    public List<ServiceType> getAllServiceTypes() {
        return serviceTypeRepository.findAll();
    }

    /**
     * Get only active service types
     */
    @Transactional(readOnly = true)
    public List<ServiceType> getActiveServiceTypes() {
        return serviceTypeRepository.findByIsActiveTrue();
    }

    /**
     * Get service type by ID
     */
    @Transactional(readOnly = true)
    public Optional<ServiceType> getServiceTypeById(UUID id) {
        return serviceTypeRepository.findById(id);
    }

    /**
     * Get service type by name
     */
    @Transactional(readOnly = true)
    public Optional<ServiceType> getServiceTypeByName(String name) {
        return serviceTypeRepository.findByName(name);
    }

    /**
     * Create a new service type
     */
    public ServiceType createServiceType(ServiceType serviceType) {
        if (serviceTypeRepository.existsByName(serviceType.getName())) {
            throw new IllegalArgumentException("Service type already exists: " + serviceType.getName());
        }
        return serviceTypeRepository.save(serviceType);
    }

    /**
     * Update an existing service type
     */
    public ServiceType updateServiceType(UUID id, ServiceType updatedServiceType) {
        return serviceTypeRepository.findById(id)
                .map(serviceType -> {
                    // Check if name changed and is already in use
                    if (updatedServiceType.getName() != null 
                            && !serviceType.getName().equalsIgnoreCase(updatedServiceType.getName())
                            && serviceTypeRepository.existsByName(updatedServiceType.getName())) {
                        throw new IllegalArgumentException("Service type already exists: " + updatedServiceType.getName());
                    }

                    if (updatedServiceType.getName() != null) {
                        serviceType.setName(updatedServiceType.getName());
                    }
                    if (updatedServiceType.getDescription() != null) {
                        serviceType.setDescription(updatedServiceType.getDescription());
                    }
                    if (updatedServiceType.getBaseRate() != null) {
                        serviceType.setBaseRate(updatedServiceType.getBaseRate());
                    }
                    if (updatedServiceType.getBaseDurationInMinutes() != null) {
                        serviceType.setBaseDurationInMinutes(updatedServiceType.getBaseDurationInMinutes());
                    }

                    return serviceTypeRepository.save(serviceType);
                })
                .orElseThrow(() -> new IllegalArgumentException("Service type not found with id: " + id));
    }

    /**
     * Deactivate a service type (soft delete)
     */
    public void deactivateServiceType(UUID id) {
        serviceTypeRepository.findById(id)
                .ifPresent(serviceType -> {
                    serviceType.setActive(false);
                    serviceTypeRepository.save(serviceType);
                });
    }

    /**
     * Reactivate a service type
     */
    public void activateServiceType(UUID id) {
        serviceTypeRepository.findById(id)
                .ifPresent(serviceType -> {
                    serviceType.setActive(true);
                    serviceTypeRepository.save(serviceType);
                });
    }

    /**
     * Check if name is available
     */
    @Transactional(readOnly = true)
    public boolean isNameAvailable(String name) {
        return !serviceTypeRepository.existsByName(name);
    }
}
