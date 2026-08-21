package org.lefab.customer.custumer.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "address")
@Validated
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
