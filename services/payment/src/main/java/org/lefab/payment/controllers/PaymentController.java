package org.lefab.payment.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lefab.payment.dtos.OrderConfirmation;
import org.lefab.payment.dtos.PaymentRequestDto;
import org.lefab.payment.dtos.PaymentResponseDto;
import org.lefab.payment.services.PaymentServices;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor

public class PaymentController {
    private final PaymentServices paymentServices;

    //get all payments
    @GetMapping
    public ResponseEntity<Page<PaymentResponseDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(paymentServices.getAllPayment(page, size));
    }

    //Create payment
    @PostMapping
      public ResponseEntity<PaymentResponseDto> createPayment(
            @RequestBody @Valid OrderConfirmation paymentRequestDto
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                paymentServices.createPayment(paymentRequestDto)
        );
    }

    //get payment id
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable @NotNull Long id){
        return ResponseEntity.ok().body(paymentServices.getPaymentById(id));
    }
}
