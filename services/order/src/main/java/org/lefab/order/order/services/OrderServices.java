package org.lefab.order.order.services;

import lombok.RequiredArgsConstructor;
import org.lefab.order.order.dtos.OrderResponseDto;
import org.lefab.order.order.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServices {
    private final OrderRepository orderRepository;

    //get all orders
    public List<OrderResponseDto> getAllOrders(){
        return List.of();
    }
}
