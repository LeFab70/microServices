package org.lefab.payment.services;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.payment.dtos.OrderConfirmation;
import org.lefab.payment.dtos.PaymentRequestDto;
import org.lefab.payment.dtos.PaymentResponseDto;
import org.lefab.payment.entities.PaymentEntity;
import org.lefab.payment.exceptions.PaymentNotFoundException;
import org.lefab.payment.kafka.PaymentConfirmation;
import org.lefab.payment.kafka.PaymentProducer;
import org.lefab.payment.mapper.PaymentMapper;
import org.lefab.payment.repositories.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServices {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProducer paymentProducer;
//
    //get all payment (paginé)

    public Page<PaymentResponseDto> getAllPayment(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        // TODO: quand cusummer, réinjecter customer
        // comme dans OrderServices (mapped -> new PaymentResponseDto(..., customer))
        return paymentRepository.findAll(pageable).map(paymentMapper::toResponse);
    }
    @Transactional
    public PaymentResponseDto createPayment(
            OrderConfirmation orderConfirmation
    ) {

        PaymentEntity payment =
                PaymentEntity.builder()
                        .orderId(orderConfirmation.orderId())
                        .orderReference(
                                orderConfirmation.orderReference()
                        )
                        .amount(orderConfirmation.totalAmount())
                        .paymentMethod(
                                orderConfirmation.paymentMethod()
                        )
                        .build();

        PaymentEntity saved =
                paymentRepository.save(payment);

        PaymentConfirmation paymentConfirmation =
                PaymentConfirmation.builder()
                        .paymentId(saved.getId())
                        .orderId(saved.getOrderId())
                        .orderReference(saved.getOrderReference())
                        .amount(saved.getAmount())
                        .paymentMethod(saved.getPaymentMethod())
                        .paymentStatus(saved.getPaymentStatus())
                        .customer(
                                orderConfirmation.customerSummaryDto()
                        )
                        .products(
                                orderConfirmation.productPurchases()
                        )
                        .build();

        paymentProducer.sendPaymentConfirmation(
                paymentConfirmation
        );

        return paymentMapper.toResponse(saved);
    }

    public @Nullable PaymentResponseDto getPaymentById(@NotNull Long id) {
        return paymentRepository.findById(id).map(paymentMapper::toResponse).orElseThrow(
                ()-> new PaymentNotFoundException("Payment not found with id: "+id)
        );
    }

//
//    @Transactional(readOnly = true)
//    public OrderResponseDto getOrderById(Long id){
//       return orderMapper.toResponse(orderRepository.findById(id).orElseThrow(
//               ()->new OrderNotFoundException("Order not found with id: "+id)
//       ));
//    }
//
//
//    @Transactional
//    public OrderResponseDto createOrder(@Valid OrderRequestDto orderRequestDto) {
//
//        // 1. valider le client (Feign, synchrone) — si absent, customer-service renvoie 404,
//        //    relayé automatiquement par notre handler FeignException
//        CustomerSummaryDto customer = customerRestClient.getCustomerById(orderRequestDto.customerId());
//
//        // 2. réserver le stock pour toutes les lignes en un seul appel
//        var purchaseRequest = orderRequestDto.orderLines().stream()
//                .map(line -> new OrderLineRequestDto(line.productId(), line.quantity()))
//                .toList();
//        List<ProductPurchaseResponseDto> purchased = productRestClient.purchaseProduct(purchaseRequest);
//
//        // 3. construire l'order + ses lignes
//        OrderEntity order = OrderEntity.builder()
//                .customerId(orderRequestDto.customerId())
//                .reference(UUID.randomUUID().toString())
//                .build(); // orderStatus/paymentMethod prennent leurs valeurs par défaut
//
//        order.setOrderLine(purchased.stream()
//                .map(p -> OrderLineEntity.builder()
//                        .productId(p.productId())
//                        .productName(p.name())
//                        .quantity((double) p.quantityPurchased())
//                        .unitPrice(p.price())
//                        .order(order)
//                        .build())
//                .toList());
//
//        OrderEntity saved = orderRepository.save(order);
//
//        // 4. construire la réponse : on réutilise le mapper pour les champs scalaires,
//        //    puis on injecte customer + les noms de produits qu'on a déjà en main
//
//
//        Map<Long, String> nameByProductId = purchased.stream()
//                .collect(Collectors.toMap(ProductPurchaseResponseDto::productId, ProductPurchaseResponseDto::name));
//
//        var lines = saved.getOrderLine().stream()
//                .map(line -> new OrderLineResponseDto(
//                        line.getId(),
//                        line.getProductId(),
//                        nameByProductId.get(line.getProductId()),
//                        line.getQuantity(),
//                        line.getUnitPrice()
//                ))
//                .toList();
//
//        OrderResponseDto mapped = orderMapper.toResponse(saved);
//    //Todo: payment
//
//        // 5. publier l'order sur Kafka'
//        BigDecimal totalAmount=purchased.stream().map(
//                productPurchaseResponseDto -> productPurchaseResponseDto.price().multiply(BigDecimal.valueOf(productPurchaseResponseDto.quantityPurchased()))
//        ).reduce(BigDecimal.ZERO,BigDecimal::add);
//
//        OrderConfirmation orderConfirmation= OrderConfirmation.builder()
//                .orderId(saved.getId())
//                .orderReference(saved.getReference())
//                .customerSummaryDto(customer)
//                .paymentMethod(saved.getPaymentMethod())
//                .totalAmount(totalAmount)
//                .status(saved.getOrderStatus())
//                .productPurchases(purchased)
//                .build();
//        //propager l'order sur Kafka'
//        orderProducer.sendOrderConfirmation(orderConfirmation);
//
//        return new OrderResponseDto(mapped.id(), mapped.orderDate(), mapped.lastUpdate(), mapped.reference(), mapped.status(), customer, lines);
//
//    }
}
