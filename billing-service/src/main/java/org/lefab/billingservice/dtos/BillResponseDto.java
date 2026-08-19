package org.lefab.billingservice.dtos;

import org.lefab.billingservice.model.Customer;

import java.time.LocalDate;
import java.util.List;

public record BillResponseDto(Long id,
                              LocalDate date,
                              Long customerId,
                              Customer customer,
                              List<ProductItemResponseDto> productsItems) {}
