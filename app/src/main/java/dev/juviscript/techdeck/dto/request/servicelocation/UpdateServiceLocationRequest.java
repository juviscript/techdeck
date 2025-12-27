package dev.juviscript.techdeck.dto.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceLocationRequest {

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String zipCode;

    private String accessNotes;

    private Boolean isPrimary;
}