package org.lefab.order.order.controllers;

import lombok.RequiredArgsConstructor;
import org.lefab.order.order.dtos.OrderResponseDto;
import org.lefab.order.order.services.OrderServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
