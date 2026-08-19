package org.lefab.billingservice.mappers;


import org.lefab.billingservice.dtos.BillRequestDto;
import org.lefab.billingservice.dtos.BillResponseDto;
import org.lefab.billingservice.entities.BillEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillingMapper {

    BillEntity toBillingEntity(BillRequestDto billRequestDto);

    BillResponseDto toBillingResponseDto(BillEntity billEntity);
}