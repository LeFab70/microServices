package org.lefab.order.order.dtos;

public record CustomerSummaryDto(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
