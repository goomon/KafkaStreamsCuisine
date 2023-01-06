package com.goomon.kafkastreams.model;

import lombok.*;

@ToString
@Getter @Setter
@AllArgsConstructor
public class PurchasePattern {

    private String zipCode;
    private String item;
    private double amount;

    public PurchasePattern() {
    }

    public static PurchasePattern build(Purchase purchase) {
        PurchasePattern purchasePattern = new PurchasePattern();
        purchasePattern.setZipCode(purchase.getZipCode());
        purchasePattern.setItem(purchase.getItemPurchased());
        purchasePattern.setAmount(purchase.getQuantity());
        return purchasePattern;
    }
}
