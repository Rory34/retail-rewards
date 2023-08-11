package com.rorysteerprojects.retailrewards.application_services;

import com.rorysteerprojects.retailrewards.api.CustomerSummaryDTO;
import com.rorysteerprojects.retailrewards.api.CustomerTransactionsDTO;
import com.rorysteerprojects.retailrewards.api.RewardsResultDTO;
import com.rorysteerprojects.retailrewards.application_services.parsers.CustomerParser;
import com.rorysteerprojects.retailrewards.application_services.parsers.RetailTransactionParser;
import com.rorysteerprojects.retailrewards.config.ResourceLookup;
import com.rorysteerprojects.retailrewards.domain.Customer;
import com.rorysteerprojects.retailrewards.domain.RewardsCalculator;
import com.rorysteerprojects.retailrewards.domain.RewardsResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
public class RetailRewardsService {
    private final CustomerParser customerParser;
    private final RetailTransactionParser retailTransactionParser;
    private final RewardsCalculator rewardsCalculator;

    public RetailRewardsService(CustomerParser customerParser,
                                RetailTransactionParser retailTransactionParser,
                                RewardsCalculator rewardsCalculator) {
        this.customerParser = customerParser;
        this.retailTransactionParser = retailTransactionParser;
        this.rewardsCalculator = rewardsCalculator;
    }

    public RewardsResultDTO calculateRewards(CustomerTransactionsDTO customerTransactions) {
        var customerParserResult = customerParser.parserCustomers(customerTransactions.getCustomers());
        var transactionParserResult = retailTransactionParser.parseTransactions(
                customerTransactions.getTransactions(),
                customerParserResult.customers()
        );

        if (!customerParserResult.errors().isEmpty() || !transactionParserResult.errors().isEmpty()) {
            return new RewardsResultDTO(
                    Collections.emptyList(),
                    Stream.concat(
                            customerParserResult.errors().stream(),
                            transactionParserResult.errors().stream())
                            .toList()
                    );
        }
        return new RewardsResultDTO(rewardsCalculator.calculate(transactionParserResult.retailTransactions())
                .stream()
                .map(summary -> buildCustomerSummaryDTO(summary, customerParserResult.customers()))
                .toList(),
                Collections.emptyList()
        );
    }

    private CustomerSummaryDTO buildCustomerSummaryDTO(RewardsResult rewardsSummary, List<Customer> customers) {
        return new CustomerSummaryDTO(
                rewardsSummary.customerId(),
                getCustomerName(customers, rewardsSummary.customerId()),
                rewardsSummary.monthlyTotals()[0],
                rewardsSummary.monthlyTotals()[1],
                rewardsSummary.monthlyTotals()[2],
                rewardsSummary.threeMonthTotal());
    }

    private String getCustomerName(List<Customer> customers, int customerId) {
        return customers.stream()
                .filter(customer -> customer.id() == customerId)
                .map(Customer::name)
                .findFirst()
                .orElse(ResourceLookup.getMessage("res_customerIdNotFound"));
    }
}
