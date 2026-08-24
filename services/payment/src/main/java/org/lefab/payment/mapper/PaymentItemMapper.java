package org.lefab.payment.mapper;

import org.lefab.payment.dtos.PaymentItemResponseDto;
import org.lefab.payment.entities.PaymentItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentItemMapper {

    @Mapping(target = "price", source = "unitPrice")
    PaymentItemResponseDto toResponse(PaymentItemEntity entity);
}
