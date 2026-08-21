package org.lefab.customer.custumer.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.lefab.customer.address.dtos.AddressRequestDto;

@Builder
public record CustomerRequestDto(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @Valid
        AddressRequestDto address
) {
}