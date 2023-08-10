package com.rorysteerprojects.retailrewards.api;

public class RetailTransactionDTO {

    private final String id;
    private final String date;
    private final String customerId;
    private final String value;

    public RetailTransactionDTO(String id, String date, String customerId, String value) {
        this.id = id;
        this.date = date;
        this.customerId = customerId;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getValue() {
        return value;
    }
}
