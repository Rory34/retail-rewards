package com.rorysteerprojects.retailrewards.application_services;

import com.rorysteerprojects.retailrewards.api.CustomerSummaryDTO;
import com.rorysteerprojects.retailrewards.api.CustomerTransactionsDTO;
import com.rorysteerprojects.retailrewards.api.RewardsResultDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RetailRewardsService {

    public RewardsResultDTO calculateRewards(CustomerTransactionsDTO customerTransactions) {
        return new RewardsResultDTO(
                List.of(new CustomerSummaryDTO(1, "c1", 0, 0, 0, 0),
                        new CustomerSummaryDTO(2, "c2", 0, 0, 0, 0)),
                Collections.emptyList());
//                List.of("Error1", "Error 2"));
    }
}
