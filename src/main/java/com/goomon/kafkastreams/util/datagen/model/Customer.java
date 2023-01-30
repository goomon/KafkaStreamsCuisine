package com.goomon.kafkastreams.util.datagen.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Customer {
    private String firstName;
    private String lastName;
    private String customerId;
    private String creditCardNumber;

    public Customer(String firstName, String lastName, String customerId, String creditCardNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerId = customerId;
        this.creditCardNumber = creditCardNumber;
    }
}
