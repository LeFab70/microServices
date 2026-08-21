package org.lefab.customer.custumer.mappers;

import org.lefab.customer.custumer.dtos.CustomerRequestDto;
import org.lefab.customer.custumer.dtos.CustomerResponseDto;
import org.lefab.customer.custumer.entities.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    CustomerEntity toCustomerEntity(CustomerRequestDto request);
    CustomerResponseDto toCustomerResponse(CustomerEntity customer);
}
