package com.goomon.kafkastreams.model;

import lombok.*;

@ToString
@Getter @Setter
@AllArgsConstructor
public class Purchase {

    private int customerId;
    private String creditCardNumber;
    private String itemPurchased;
    private int quantity;
    private double price;
    private String zipCode;

    public Purchase() {
    }
}
