package org.lefab.order.clients;

import org.lefab.order.orderItem.dtos.OrderLineRequestDto;
import org.lefab.order.orderItem.dtos.ProductPurchaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductRestClient {
    @PostMapping("/api/v1/product/purchase")
    List<ProductPurchaseResponseDto> purchaseProduct(
            @RequestBody List<OrderLineRequestDto> orderLineRequestDto
            );
}
