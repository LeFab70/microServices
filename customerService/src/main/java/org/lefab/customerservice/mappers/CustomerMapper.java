package org.lefab.customerservice.mappers;

import org.lefab.customerservice.dtos.CustomerRequestDto;
import org.lefab.customerservice.dtos.CustomerResponseDto;
import org.lefab.customerservice.entities.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerEntity toCustomerEntity(CustomerRequestDto customerRequestDto);

    CustomerResponseDto toCustomerResponseDto(CustomerEntity customerEntity);
}