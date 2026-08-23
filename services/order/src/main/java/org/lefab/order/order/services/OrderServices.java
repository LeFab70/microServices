package org.lefab.order.order.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.order.clients.CustomerRestClient;
import org.lefab.order.clients.ProductRestClient;
import org.lefab.order.kafka.OrderConfirmation;
import org.lefab.order.kafka.OrderProducer;
import org.lefab.order.order.dtos.CustomerSummaryDto;
import org.lefab.order.order.dtos.OrderRequestDto;
import org.lefab.order.order.dtos.OrderResponseDto;
import org.lefab.order.order.entities.OrderEntity;
import org.lefab.order.order.mapper.OrderMapper;
import org.lefab.order.order.repositories.OrderRepository;
import org.lefab.order.orderItem.dtos.OrderLineRequestDto;
import org.lefab.order.orderItem.dtos.OrderLineResponseDto;
import org.lefab.order.orderItem.dtos.ProductPurchaseResponseDto;
import org.lefab.order.orderItem.entities.OrderLineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServices {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerRestClient customerRestClient;
    private final ProductRestClient productRestClient;
    private final OrderProducer orderProducer;

    //get all orders (paginé)
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrders(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).map(
                order -> {
                    CustomerSummaryDto customer = customerRestClient.getCustomerById(order.getCustomerId());
                    OrderResponseDto mapped = orderMapper.toResponse(order);
                    return new OrderResponseDto(
                            mapped.id(),
                            mapped.orderDate(),
                            mapped.lastUpdate(),
                            mapped.reference(),
                            mapped.status(),
                            customer,
                            mapped.orderLine()
                    );
                }
        );
    }



    @Transactional
    public OrderResponseDto createOrder(@Valid OrderRequestDto orderRequestDto) {

        // 1. valider le client (Feign, synchrone) — si absent, customer-service renvoie 404,
        //    relayé automatiquement par notre handler FeignException
        CustomerSummaryDto customer = customerRestClient.getCustomerById(orderRequestDto.customerId());

        // 2. réserver le stock pour toutes les lignes en un seul appel
        var purchaseRequest = orderRequestDto.orderLines().stream()
                .map(line -> new OrderLineRequestDto(line.productId(), line.quantity()))
                .toList();
        List<ProductPurchaseResponseDto> purchased = productRestClient.purchaseProduct(purchaseRequest);

        // 3. construire l'order + ses lignes
        OrderEntity order = OrderEntity.builder()
                .customerId(orderRequestDto.customerId())
                .reference(UUID.randomUUID().toString())
                .build(); // orderStatus/paymentMethod prennent leurs valeurs par défaut

        order.setOrderLine(purchased.stream()
                .map(p -> OrderLineEntity.builder()
                        .productId(p.productId())
                        .productName(p.name())
                        .quantity((double) p.quantityPurchased())
                        .unitPrice(p.price())
                        .order(order)
                        .build())
                .toList());

        OrderEntity saved = orderRepository.save(order);

        // 4. construire la réponse : on réutilise le mapper pour les champs scalaires,
        //    puis on injecte customer + les noms de produits qu'on a déjà en main


        Map<Long, String> nameByProductId = purchased.stream()
                .collect(Collectors.toMap(ProductPurchaseResponseDto::productId, ProductPurchaseResponseDto::name));

        var lines = saved.getOrderLine().stream()
                .map(line -> new OrderLineResponseDto(
                        line.getId(),
                        line.getProductId(),
                        nameByProductId.get(line.getProductId()),
                        line.getQuantity(),
                        line.getUnitPrice()
                ))
                .toList();

        OrderResponseDto mapped = orderMapper.toResponse(saved);
    //Todo: payment

        // 5. publier l'order sur Kafka'
        BigDecimal totalAmount=purchased.stream().map(
                productPurchaseResponseDto -> productPurchaseResponseDto.price().multiply(BigDecimal.valueOf(productPurchaseResponseDto.quantityPurchased()))
        ).reduce(BigDecimal.ZERO,BigDecimal::add);

        OrderConfirmation orderConfirmation= OrderConfirmation.builder()
                .orderId(saved.getId())
                .orderReference(saved.getReference())
                .customerSummaryDto(customer)
                .paymentMethod(saved.getPaymentMethod())
                .totalAmount(totalAmount)
                .status(saved.getOrderStatus())
                .productPurchases(purchased)
                .build();
        orderProducer.sendOrderConfirmation(orderConfirmation);

        return new OrderResponseDto(mapped.id(), mapped.orderDate(), mapped.lastUpdate(), mapped.reference(), mapped.status(), customer, lines);

    }
}
