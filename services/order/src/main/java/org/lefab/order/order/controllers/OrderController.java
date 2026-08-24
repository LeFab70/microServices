package org.lefab.order.order.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lefab.order.order.dtos.OrderRequestDto;
import org.lefab.order.order.dtos.OrderResponseDto;
import org.lefab.order.order.services.OrderServices;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Orchestration des commandes")
public class OrderController {
    private final OrderServices orderServices;

    //get all orders
    @GetMapping
    @Operation(summary = "Lister les commandes (paginé)")
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(orderServices.getAllOrders(page, size));
    }

    //Create order
    @PostMapping
    @Operation(summary = "Créer une commande (valide le client, réserve le stock, publie un événement Kafka)")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto orderRequestDto
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderServices.createOrder(orderRequestDto)
        );
    }

    //get order by id
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable @NotNull Long id){
        return ResponseEntity.ok().body(orderServices.getOrderById(id));
    }
}
