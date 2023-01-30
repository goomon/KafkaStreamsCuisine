package com.goomon.kafkastreams.model;

import lombok.*;

import java.util.Date;

@ToString
@Getter @Setter
public class PurchasePattern {

    private String zipCode;
    private String item;
    private Date date;
    private double amount;

    private PurchasePattern(Builder builder) {
        zipCode = builder.zipCode;
        item = builder.item;
        date = builder.date;
        amount = builder.amount;

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder(Purchase purchase){
        return new Builder(purchase);

    }

    public static final class Builder {
        private String zipCode;
        private String item;
        private Date date;
        private double amount;

        private  Builder() {
        }

        private Builder(Purchase purchase) {
            this.zipCode = purchase.getZipCode();
            this.item = purchase.getItemPurchased();
            this.date = purchase.getPurchaseDate();
            this.amount = purchase.getPrice() * purchase.getQuantity();
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Builder item(String val) {
            item = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public PurchasePattern build() {
            return new PurchasePattern(this);
        }
    }
}
