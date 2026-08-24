package org.lefab.payment.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record CustomerSummaryDto(
        String id,
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotBlank(message = "Phone number is required")
        String phoneNumber,
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email
) {
}
