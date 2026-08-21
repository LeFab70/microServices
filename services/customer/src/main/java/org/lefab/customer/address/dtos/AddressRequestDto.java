package org.lefab.customer.address.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddressRequestDto(

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Zip code is required")
        String zipCode
) {
}
