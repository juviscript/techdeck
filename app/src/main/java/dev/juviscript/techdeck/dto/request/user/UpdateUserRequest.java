package dev.juviscript.techdeck.dto.request.user;

import dev.juviscript.techdeck.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
    private Boolean isActive;

    // Note: Email and password changes should have separate endpoints
    // with proper verification flows
}