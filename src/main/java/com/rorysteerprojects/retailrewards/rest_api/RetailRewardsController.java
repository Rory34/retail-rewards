package com.rorysteerprojects.retailrewards.rest_api;

import com.rorysteerprojects.config.ResourceLookup;
import com.rorysteerprojects.retailrewards.api.CustomerTransactionsDTO;
import com.rorysteerprojects.retailrewards.api.RewardsResultDTO;
import com.rorysteerprojects.retailrewards.application_services.RetailRewardsService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


@RestController
public class RetailRewardsController {

    private final RetailRewardsService retailRewardsService;


    public RetailRewardsController(RetailRewardsService retailRewardsService) {
        this.retailRewardsService = retailRewardsService;
    }

    @PostMapping("/calculate-rewards")
    public ResponseEntity<RewardsResultDTO> calculateRewards(@RequestBody CustomerTransactionsDTO customerTransactions) {
        if (isInvalidRequestBody(customerTransactions)) {
            return ResponseEntity.badRequest().body(new RewardsResultDTO(Collections.emptyList(),
                    List.of(ResourceLookup.getMessage("res_missingLists"))));
        }

        RewardsResultDTO rewardsResult = retailRewardsService.calculateRewards(customerTransactions);

        return rewardsResult.getErrors().isEmpty() ?
                ResponseEntity.ok(rewardsResult) :
                ResponseEntity
                        .status(HttpStatusCode.valueOf(422))
                        .body(rewardsResult);
    }

    private boolean isInvalidRequestBody(CustomerTransactionsDTO customerTransactions) {
        return customerTransactions == null ||
                customerTransactions.getCustomers() == null ||
                customerTransactions.getTransactions() == null ||
                !customerTransactions.getTransactions().isEmpty() && customerTransactions.getCustomers().isEmpty();
    }

}
