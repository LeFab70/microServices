package org.lefab.order.orderItem.mapper;

import org.lefab.order.orderItem.dtos.OrderLineResponseDto;
import org.lefab.order.orderItem.entities.OrderLineEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderLineMapper {

    @Mapping(target = "price", source = "unitPrice")
    @Mapping(target = "productName", ignore = true)
    OrderLineResponseDto toResponse(OrderLineEntity entity);
}