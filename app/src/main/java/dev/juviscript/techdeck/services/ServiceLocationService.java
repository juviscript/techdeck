package dev.juviscript.techdeck.services;

import dev.juviscript.techdeck.models.Customer;
import dev.juviscript.techdeck.models.ServiceLocation;
import dev.juviscript.techdeck.repositories.CustomerRepository;
import dev.juviscript.techdeck.repositories.ServiceLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceLocationService {

    private final ServiceLocationRepository serviceLocationRepository;
    private final CustomerRepository customerRepository;

    /**
     * Get all service locations for a customer
     */
    @Transactional(readOnly = true)
    public List<ServiceLocation> getByCustomerId(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with id: " + customerId);
        }
        return serviceLocationRepository.findByCustomerId(customerId);
    }

    /**
     * Get a service location by ID
     */
    @Transactional(readOnly = true)
    public Optional<ServiceLocation> getById(UUID locationId) {
        return serviceLocationRepository.findById(locationId);
    }

    /**
     * Add a service location to a customer
     */
    public ServiceLocation addToCustomer(UUID customerId, ServiceLocation location) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        location.setCustomer(customer);

        // If this location is marked as primary, set all others to non-primary
        if (location.isPrimary()) {
            clearPrimaryFlag(customerId);
        }

        return serviceLocationRepository.save(location);
    }

    /**
     * Update a service location
     */
    public ServiceLocation update(UUID locationId, ServiceLocation updatedLocation) {
        return serviceLocationRepository.findById(locationId)
                .map(location -> {
                    if (updatedLocation.getAddressLine1() != null) {
                        location.setAddressLine1(updatedLocation.getAddressLine1());
                    }
                    if (updatedLocation.getAddressLine2() != null) {
                        location.setAddressLine2(updatedLocation.getAddressLine2());
                    }
                    if (updatedLocation.getCity() != null) {
                        location.setCity(updatedLocation.getCity());
                    }
                    if (updatedLocation.getState() != null) {
                        location.setState(updatedLocation.getState());
                    }
                    if (updatedLocation.getZipCode() != null) {
                        location.setZipCode(updatedLocation.getZipCode());
                    }
                    if (updatedLocation.getAccessNotes() != null) {
                        location.setAccessNotes(updatedLocation.getAccessNotes());
                    }

                    // Handle primary flag change
                    if (updatedLocation.isPrimary() && !location.isPrimary()) {
                        clearPrimaryFlag(location.getCustomer().getId());
                    }
                    location.setPrimary(updatedLocation.isPrimary());

                    return serviceLocationRepository.save(location);
                })
                .orElseThrow(() -> new IllegalArgumentException("Service location not found with id: " + locationId));
    }

    /**
     * Delete a service location
     */
    public void delete(UUID locationId) {
        if (!serviceLocationRepository.existsById(locationId)) {
            throw new IllegalArgumentException("Service location not found with id: " + locationId);
        }
        serviceLocationRepository.deleteById(locationId);
    }

    /**
     * Clear the primary flag on all locations for a customer
     */
    private void clearPrimaryFlag(UUID customerId) {
        List<ServiceLocation> locations = serviceLocationRepository.findByCustomerId(customerId);
        locations.forEach(loc -> loc.setPrimary(false));
        serviceLocationRepository.saveAll(locations);
    }
}