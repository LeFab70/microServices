package org.lefab.customer.address.dtos;

public record AddressResponse(
        String street,

        String city,

        String state,

        String zipCode
) {
}
