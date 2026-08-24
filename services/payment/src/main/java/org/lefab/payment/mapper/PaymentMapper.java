package org.lefab.payment.mapper;

import org.lefab.payment.dtos.PaymentRequestDto;
import org.lefab.payment.dtos.PaymentResponseDto;
import org.lefab.payment.entities.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PaymentItemMapper.class})
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    PaymentEntity toEntity(PaymentRequestDto request);

    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "status", source = "paymentStatus")
    @Mapping(target = "orderReference", source = "orderReference")
    @Mapping(target = "customer", ignore = true)
    PaymentResponseDto toResponse(PaymentEntity entity);
}
