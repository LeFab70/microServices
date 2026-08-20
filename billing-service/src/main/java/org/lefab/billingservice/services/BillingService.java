package org.lefab.billingservice.services;

import lombok.RequiredArgsConstructor;

import org.lefab.billingservice.dtos.BillResponseDto;
import org.lefab.billingservice.entities.BillEntity;
import org.lefab.billingservice.feign.CustomerRestClient;
import org.lefab.billingservice.feign.ProductItemRestClient;
import org.lefab.billingservice.mappers.BillingMapper;
import org.lefab.billingservice.model.Customer;
import org.lefab.billingservice.model.Product;
import org.lefab.billingservice.repositories.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

  private final BillingMapper billingMapper;
  private final BillRepository billRepository;
  private final CustomerRestClient customerRestClient;
  private final ProductItemRestClient productItemRestClient;

  //    public CustomerResponseDto saveCustomer(CustomerRequestDto customerRequestDto) {
  //       boolean customerExists = customerRepository.existsByName(customerRequestDto.name());
  //       if (customerExists) {
  //           throw new CustomerAlreadyExistsException("Customer already exists");
  //       }
  //       CustomerEntity savedCustomer =
  // CustomerEntity.builder().name(customerRequestDto.name()).build();
  //       return customerMapper.toCustomerResponseDto(savedCustomer);
  //    }

  // getAllBilling
  @Transactional(readOnly = true)
  public List<BillResponseDto> getAllBilling() {

      return billRepository.findAll()
              .stream()
              .map(this::loadRelations)
              .map(billingMapper::toBillingResponseDto)
              .toList();
  }

    private BillEntity loadRelations(BillEntity bill) {

        Customer customer =
                customerRestClient.findCustomerById(
                        bill.getCustomerId()
                );

        bill.setCustomer(customer);

        bill.getProductsItems().forEach(item -> {

            Product product =
                    productItemRestClient.findProductById(
                            item.getProductId()
                    );

            item.setProduct(product);
        });

        return bill;
    }
}