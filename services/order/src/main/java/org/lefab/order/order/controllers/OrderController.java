package org.lefab.order.order.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.lefab.order.order.dtos.OrderRequestDto;
import org.lefab.order.order.dtos.OrderResponseDto;
import org.lefab.order.order.services.OrderServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServices orderServices;

    //get all orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(){
        return ResponseEntity.ok(orderServices.getAllOrders());
    }

    //Create order
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto orderRequestDto
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderServices.createOrder(orderRequestDto)
        );
    }

}
