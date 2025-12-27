package dev.juviscript.techdeck.controllers;

import dev.juviscript.techdeck.dto.request.customer.CreateCustomerRequest;
import dev.juviscript.techdeck.dto.request.customer.CreateServiceLocationRequest;
import dev.juviscript.techdeck.dto.request.customer.UpdateCustomerRequest;
import dev.juviscript.techdeck.dto.request.customer.UpdateServiceLocationRequest;
import dev.juviscript.techdeck.dto.response.CustomerResponse;
import dev.juviscript.techdeck.dto.response.ServiceLocationResponse;
import dev.juviscript.techdeck.mappers.CustomerMapper;
import dev.juviscript.techdeck.models.Customer;
import dev.juviscript.techdeck.models.ServiceLocation;
import dev.juviscript.techdeck.services.CustomerService;
import dev.juviscript.techdeck.services.ServiceLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceLocationService serviceLocationService;
    private final CustomerMapper customerMapper;

    // ==========================================
    // Customer Endpoints
    // ==========================================

    /**
     * GET /api/v1/customers
     * Get all customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
        return ResponseEntity.ok(customers);
    }

    /**
     * GET /api/v1/customers/{id}
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id)
                .map(customerMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/customers
     * Create a new customer
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerMapper.toResponse(savedCustomer));
    }

    /**
     * PUT /api/v1/customers/{id}
     * Update an existing customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        return customerService.getCustomerById(id)
                .map(existingCustomer -> {
                    if (request.getFirstName() != null) {
                        existingCustomer.setFirstName(request.getFirstName());
                    }
                    if (request.getLastName() != null) {
                        existingCustomer.setLastName(request.getLastName());
                    }
                    if (request.getEmail() != null) {
                        existingCustomer.setEmail(request.getEmail());
                    }
                    if (request.getPhoneNumber() != null) {
                        existingCustomer.setPhoneNumber(request.getPhoneNumber());
                    }
                    if (request.getNotes() != null) {
                        existingCustomer.setNotes(request.getNotes());
                    }

                    Customer updatedCustomer = customerService.updateCustomer(id, existingCustomer);
                    return ResponseEntity.ok(customerMapper.toResponse(updatedCustomer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/customers/{id}
     * Delete a customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/customers/check-email?email={email}
     * Check if email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        return ResponseEntity.ok(customerService.isEmailAvailable(email));
    }

    // ==========================================
    // Service Location Endpoints
    // ==========================================

    /**
     * GET /api/v1/customers/{customerId}/locations
     * Get all service locations for a customer
     */
    @GetMapping("/{customerId}/locations")
    public ResponseEntity<List<ServiceLocationResponse>> getServiceLocations(@PathVariable UUID customerId) {
        try {
            List<ServiceLocationResponse> locations = serviceLocationService.getByCustomerId(customerId)
                    .stream()
                    .map(customerMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(locations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/v1/customers/{customerId}/locations
     * Add a service location to a customer
     */
    @PostMapping("/{customerId}/locations")
    public ResponseEntity<ServiceLocationResponse> addServiceLocation(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateServiceLocationRequest request) {

        try {
            ServiceLocation location = customerMapper.toEntity(request);
            ServiceLocation savedLocation = serviceLocationService.addToCustomer(customerId, location);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(customerMapper.toResponse(savedLocation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/v1/customers/{customerId}/locations/{locationId}
     * Update a service location
     */
    @PutMapping("/{customerId}/locations/{locationId}")
    public ResponseEntity<ServiceLocationResponse> updateServiceLocation(
            @PathVariable UUID customerId,
            @PathVariable UUID locationId,
            @Valid @RequestBody UpdateServiceLocationRequest request) {

        try {
            // Build updated location from request
            ServiceLocation updatedLocation = new ServiceLocation();
            updatedLocation.setAddressLine1(request.getAddressLine1());
            updatedLocation.setAddressLine2(request.getAddressLine2());
            updatedLocation.setCity(request.getCity());
            updatedLocation.setState(request.getState());
            updatedLocation.setZipCode(request.getZipCode());
            updatedLocation.setAccessNotes(request.getAccessNotes());
            if (request.getIsPrimary() != null) {
                updatedLocation.setPrimary(request.getIsPrimary());
            }

            ServiceLocation savedLocation = serviceLocationService.update(locationId, updatedLocation);
            return ResponseEntity.ok(customerMapper.toResponse(savedLocation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/customers/{customerId}/locations/{locationId}
     * Delete a service location
     */
    @DeleteMapping("/{customerId}/locations/{locationId}")
    public ResponseEntity<Void> deleteServiceLocation(
            @PathVariable UUID customerId,
            @PathVariable UUID locationId) {

        try {
            serviceLocationService.delete(locationId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}