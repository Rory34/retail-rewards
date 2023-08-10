package com.rorysteerprojects.retailrewards.api;

import java.util.List;

public class CustomerTransactionsDTO {

    private final List<CustomerDTO> customers;
    private final List<RetailTransactionDTO> transactions;

    public CustomerTransactionsDTO(List<CustomerDTO> customers, List<RetailTransactionDTO> transactions) {
        this.customers = customers;
        this.transactions = transactions;
    }

    public List<CustomerDTO> getCustomers() {
        return customers;
    }

    public List<RetailTransactionDTO> getTransactions() {
        return transactions;
    }
}
