package org.lefab.customerservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerRequestDto(
@NotBlank(message = "Name cannot be blank")
@Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
@NotNull(message = "Name cannot be null")
        String name
) {}
