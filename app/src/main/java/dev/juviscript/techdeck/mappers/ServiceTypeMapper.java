package dev.juviscript.techdeck.mappers;

import dev.juviscript.techdeck.dto.request.servicetype.CreateServiceTypeRequest;
import dev.juviscript.techdeck.dto.response.ServiceTypeResponse;
import dev.juviscript.techdeck.models.ServiceType;
import dev.juviscript.techdeck.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeMapper {

    /**
     * Convert CreateServiceTypeRequest to ServiceType entity
     */
    public ServiceType toEntity(CreateServiceTypeRequest request) {
        ServiceType serviceType = new ServiceType();
        serviceType.setName(StringUtils.capitalizeWords(request.getName()));
        serviceType.setDescription(StringUtils.trim(request.getDescription()));
        serviceType.setBaseRate(request.getBaseRate());
        serviceType.setBaseDurationInMinutes(request.getBaseDurationInMinutes());
        serviceType.setActive(true);
        return serviceType;
    }

    /**
     * Convert ServiceType entity to ServiceTypeResponse DTO
     */
    public ServiceTypeResponse toResponse(ServiceType serviceType) {
        return ServiceTypeResponse.builder()
                .id(serviceType.getId())
                .name(serviceType.getName())
                .description(serviceType.getDescription())
                .baseRate(serviceType.getBaseRate())
                .baseDurationInMinutes(serviceType.getBaseDurationInMinutes())
                .isActive(serviceType.isActive())
                .createdAt(serviceType.getCreatedAt())
                .updatedAt(serviceType.getUpdatedAt())
                .build();
    }
}
