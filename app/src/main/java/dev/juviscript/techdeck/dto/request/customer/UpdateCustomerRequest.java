package dev.juviscript.techdeck.dto.request.customer;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {

    private String firstName;

    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String notes;
}