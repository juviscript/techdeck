package dev.juviscript.techdeck.mappers;

import dev.juviscript.techdeck.dto.request.user.CreateUserRequest;
import dev.juviscript.techdeck.dto.response.UserResponse;
import dev.juviscript.techdeck.models.User;
import dev.juviscript.techdeck.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponse (or UserDTO)
     */
    public UserResponse toDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert CreateUserRequest to User entity
     * Note: Password should be encoded before saving
     */
    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(StringUtils.capitalizeFirst(request.getFirstName()));
        user.setLastName(StringUtils.capitalizeFirst(request.getLastName()));
        user.setEmail(StringUtils.normalizeEmail(request.getEmail()));
        user.setPhoneNumber(StringUtils.normalizePhone(request.getPhoneNumber()));
        user.setPassword(request.getPassword()); // Encode this in the service!
        user.setRole(request.getRole());
        user.setActive(true);
        return user;
    }
}