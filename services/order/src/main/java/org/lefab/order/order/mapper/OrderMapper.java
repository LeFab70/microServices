package org.lefab.order.order.mapper;

import org.lefab.order.order.dtos.OrderResponseDto;
import org.lefab.order.order.entities.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                org.lefab.order.orderItem.mapper.OrderLineMapper.class,
        }
)
public interface OrderMapper {

    @Mapping(target = "orderDate", source = "createAt")
    @Mapping(target = "lastUpdate", source = "updateAt")
    @Mapping(target = "status", source = "orderStatus")
    @Mapping(target = "customer", ignore = true)
    OrderResponseDto toResponse(OrderEntity order);
}