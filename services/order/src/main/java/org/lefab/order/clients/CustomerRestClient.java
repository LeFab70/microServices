package org.lefab.order.clients;

import jakarta.validation.constraints.NotNull;
import org.lefab.order.order.dtos.CustomerSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/api/v1/customer/{id}")
    CustomerSummaryDto getCustomerById(@PathVariable("id") @NotNull String id);
}
