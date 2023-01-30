package com.goomon.kafkastreams.util.datagen.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Store {
    private String employeeId;
    private String zipCode;
    private String storeId;
    private String department;

    public Store(String employeeId, String zipCode, String storeId, String department) {
        this.employeeId = employeeId;
        this.zipCode = zipCode;
        this.storeId = storeId;
        this.department = department;
    }
}
