package com.rorysteerprojects.retailrewards.api;

import java.util.List;

public class RewardsResultDTO {
    private final List<CustomerSummaryDTO> customerSummaries;
    private final List<String> errors;

    public RewardsResultDTO(List<CustomerSummaryDTO> customerSummaries, List<String> errors) {

        this.customerSummaries = customerSummaries;
        this.errors = errors;
    }

    public List<CustomerSummaryDTO> getCustomerSummaries() {
        return customerSummaries;
    }

    public List<String> getErrors() {
        return errors;
    }
}
