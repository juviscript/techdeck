package dev.juviscript.techdeck.mappers;

import dev.juviscript.techdeck.dto.request.customer.CreateCustomerRequest;
import dev.juviscript.techdeck.dto.request.customer.CreateServiceLocationRequest;
import dev.juviscript.techdeck.dto.response.CustomerResponse;
import dev.juviscript.techdeck.dto.response.ServiceLocationResponse;
import dev.juviscript.techdeck.models.Customer;
import dev.juviscript.techdeck.models.ServiceLocation;
import dev.juviscript.techdeck.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerMapper {

    /**
     * Convert CreateCustomerRequest to Customer entity
     */
    public Customer toEntity(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(StringUtils.capitalizeFirst(request.getFirstName()));
        customer.setLastName(StringUtils.capitalizeFirst(request.getLastName()));
        customer.setEmail(StringUtils.normalizeEmail(request.getEmail()));
        customer.setPhoneNumber(StringUtils.normalizePhone(request.getPhoneNumber()));
        customer.setNotes(StringUtils.trim(request.getNotes()));

        // Map service locations if provided
        if (request.getServiceLocations() != null && !request.getServiceLocations().isEmpty()) {
            List<ServiceLocation> locations = new ArrayList<>();
            for (CreateServiceLocationRequest locRequest : request.getServiceLocations()) {
                ServiceLocation location = toEntity(locRequest);
                location.setCustomer(customer); // Set the back-reference
                locations.add(location);
            }
            customer.setServiceLocations(locations);
        }

        return customer;
    }

    /**
     * Convert CreateServiceLocationRequest to ServiceLocation entity
     */
    public ServiceLocation toEntity(CreateServiceLocationRequest request) {
        ServiceLocation location = new ServiceLocation();
        location.setAddressLine1(StringUtils.normalizeAddress(request.getAddressLine1()));
        location.setAddressLine2(StringUtils.normalizeAddress(request.getAddressLine2()));
        location.setCity(StringUtils.normalizeCity(request.getCity()));
        location.setState(StringUtils.normalizeState(request.getState()));
        location.setZipCode(StringUtils.normalizeZipCode(request.getZipCode()));
        location.setAccessNotes(StringUtils.trim(request.getAccessNotes()));
        location.setPrimary(request.isPrimary());
        return location;
    }

    /**
     * Convert Customer entity to CustomerResponse DTO
     */
    public CustomerResponse toResponse(Customer customer) {
        List<ServiceLocationResponse> locationResponses = new ArrayList<>();

        if (customer.getServiceLocations() != null) {
            for (ServiceLocation location : customer.getServiceLocations()) {
                locationResponses.add(toResponse(location));
            }
        }

        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .notes(customer.getNotes())
                .serviceLocations(locationResponses)
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    /**
     * Convert ServiceLocation entity to ServiceLocationResponse DTO
     */
    public ServiceLocationResponse toResponse(ServiceLocation location) {
        return ServiceLocationResponse.builder()
                .id(location.getId())
                .addressLine1(location.getAddressLine1())
                .addressLine2(location.getAddressLine2())
                .city(location.getCity())
                .state(location.getState())
                .zipCode(location.getZipCode())
                .accessNotes(location.getAccessNotes())
                .isPrimary(location.isPrimary())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
}