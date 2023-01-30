package com.goomon.kafkastreams.model;

import lombok.*;

import java.util.Date;
import java.util.Objects;

@ToString
@Getter @Setter
public class Purchase {

    private String firstName;
    private String lastName;
    private String customerId;
    private String creditCardNumber;
    private String itemPurchased;
    private String department;
    private String employeeId;
    private int quantity;
    private double price;
    private Date purchaseDate;
    private String zipCode;
    private String storeId;

    private Purchase(Builder builder) {
        firstName = builder.firstName;
        lastName = builder.lastName;
        customerId = builder.customerId;
        creditCardNumber = builder.creditCardNumber;
        itemPurchased = builder.itemPurchased;
        quantity = builder.quantity;
        price = builder.price;
        purchaseDate = builder.purchaseDate;
        zipCode = builder.zipCode;
        employeeId = builder.employeeId;
        department = builder.department;
        storeId = builder.storeId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Purchase copy) {
        Builder builder = new Builder();
        builder.firstName = copy.firstName;
        builder.lastName = copy.lastName;
        builder.creditCardNumber = copy.creditCardNumber;
        builder.itemPurchased = copy.itemPurchased;
        builder.quantity = copy.quantity;
        builder.price = copy.price;
        builder.purchaseDate = copy.purchaseDate;
        builder.zipCode = copy.zipCode;
        builder.customerId = copy.customerId;
        builder.department = copy.department;
        builder.employeeId = copy.employeeId;
        builder.storeId = copy.storeId;

        return builder;
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private String customerId;
        private String creditCardNumber;
        private String itemPurchased;
        private int quantity;
        private double price;
        private Date purchaseDate;
        private String zipCode;
        private String department;
        private String employeeId;
        private String storeId;

        private static final String CC_NUMBER_REPLACEMENT="xxxx-xxxx-xxxx-";

        private Builder() {
        }

        public Builder firstName(String val) {
            this.firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            this.lastName = val;
            return this;
        }

        public Builder maskCreditCard(){
            Objects.requireNonNull(this.creditCardNumber, "Credit Card can't be null");
            String[] parts = this.creditCardNumber.split("-");
            if (parts.length < 4 ) {
                this.creditCardNumber = "xxxx";
            } else {
                String last4Digits = this.creditCardNumber.split("-")[3];
                this.creditCardNumber = CC_NUMBER_REPLACEMENT + last4Digits;
            }
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder storeId(String storeId) {
            this.storeId = storeId;
            return this;
        }

        public Builder creditCardNumber(String val) {
            this.creditCardNumber = val;
            return this;
        }

        public Builder itemPurchased(String val) {
            this.itemPurchased = val;
            return this;
        }

        public Builder quantity(int val) {
            this.quantity = val;
            return this;
        }

        public Builder price(double val) {
            this.price = val;
            return this;
        }

        public Builder purchaseDate(Date val) {
            this.purchaseDate = val;
            return this;
        }

        public Builder zipCode(String val) {
            this.zipCode = val;
            return this;
        }

        public Purchase build() {
            return new Purchase(this);
        }
    }
}
