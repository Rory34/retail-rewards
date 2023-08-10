package com.rorysteerprojects.retailrewards.api;

public class CustomerSummaryDTO {
    private final int customerId;
    private final String customerName;
    private final int month1Rewards;
    private final int month2Rewards;
    private final int month3Rewards;
    private final int totalRewards;

    public CustomerSummaryDTO(int customerId,
                              String customerName,
                              int month1Rewards,
                              int month2Rewards,
                              int month3Rewards,
                              int totalRewards) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.month1Rewards = month1Rewards;
        this.month2Rewards = month2Rewards;
        this.month3Rewards = month3Rewards;
        this.totalRewards = totalRewards;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getMonth1Rewards() {
        return month1Rewards;
    }

    public int getMonth2Rewards() {
        return month2Rewards;
    }

    public int getMonth3Rewards() {
        return month3Rewards;
    }

    public int getTotalRewards() {
        return totalRewards;
    }
}
