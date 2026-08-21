package org.lefab.customer.custumer.dtos;

import org.lefab.customer.address.dtos.AddressResponse;

public record CustomerResponseDto(
        String id,

        String firstName,

        String lastName,

        String email,

        AddressResponse address
) {
}
