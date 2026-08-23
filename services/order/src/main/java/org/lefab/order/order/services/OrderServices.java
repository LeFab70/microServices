package org.lefab.order.order.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.order.order.dtos.OrderRequestDto;
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

    public  OrderResponseDto createOrder(@Valid OrderRequestDto orderRequestDto) {
        return null;
    }
}
