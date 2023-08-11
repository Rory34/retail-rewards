package com.rorysteerprojects.retailrewards.application_services;

import com.rorysteerprojects.retailrewards.api.CustomerDTO;
import com.rorysteerprojects.retailrewards.api.CustomerTransactionsDTO;
import com.rorysteerprojects.retailrewards.api.RetailTransactionDTO;
import com.rorysteerprojects.retailrewards.application_services.parsers.CustomerParser;
import com.rorysteerprojects.retailrewards.application_services.parsers.RetailTransactionParser;
import com.rorysteerprojects.retailrewards.domain.RewardsCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RetailRewardsServiceTest {

    private final RetailRewardsService service = new RetailRewardsService(
            new CustomerParser(),
            new RetailTransactionParser(),
            new RewardsCalculator()
    );

    @Test
    public void errorsInCustomerListOnlyAreReturned() {
        var customerTransaction = new CustomerTransactionsDTO(
                List.of(
                        new CustomerDTO("1", "Customer 1"),
                        new CustomerDTO(null, null)
                ),
                List.of(
                        new RetailTransactionDTO("101", "2023-08-12", "1", "51.0")
                )
        );

        var result = service.calculateRewards(customerTransaction);

        assertTrue(result.getCustomerSummaries().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getCustomers().get(1).toString() + " : has an invalid customer id.")));

    }

    @Test
    public void errorsInTransactionListOnlyAreReturned() {
        var customerTransaction = new CustomerTransactionsDTO(
                List.of(
                        new CustomerDTO("1", "Customer 1")
                ),
                List.of(
                        new RetailTransactionDTO("101", "2023-08-12", "1", "51.0"),
                        new RetailTransactionDTO(null, null, null, null),
                        new RetailTransactionDTO("102", "2023-08-12", "2", "51.0")
                )
        );

        var result = service.calculateRewards(customerTransaction);

        assertTrue(result.getCustomerSummaries().isEmpty());
        assertEquals(5, result.getErrors().size());
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid transaction id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid transaction date.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid customer id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid value.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(2).toString() + " : has a Customer id not found in the list of Customers.")));
    }

    @Test
    public void errorsInCustomerListAndTransactionListAreCombined() {
        var customerTransaction = new CustomerTransactionsDTO(
                List.of(
                        new CustomerDTO("1", "Customer 1"),
                        new CustomerDTO(null, null)
                ),
                List.of(
                        new RetailTransactionDTO("101", "2023-08-12", "1", "51.0"),
                        new RetailTransactionDTO(null, null, null, null),
                        new RetailTransactionDTO("102", "2023-08-12", "2", "51.0")
                )
        );

        var result = service.calculateRewards(customerTransaction);

        assertTrue(result.getCustomerSummaries().isEmpty());
        assertEquals(5, result.getErrors().size());
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getCustomers().get(1).toString() + " : has an invalid customer id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid transaction id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid transaction date.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid customer id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid value.")));
    }

    @Test
    public void transactionsWithCustomerIdNotInCustomerListIsReported() {
        var customerTransaction = new CustomerTransactionsDTO(
                List.of(
                        new CustomerDTO("1", "Customer 1")
                ),
                List.of(
                        new RetailTransactionDTO("101", "2023-08-12", "1", "51.0"),
                        new RetailTransactionDTO(null, null, null, null),
                        new RetailTransactionDTO("102", "2023-08-12", "2", "51.0")
                )
        );

        var result = service.calculateRewards(customerTransaction);

        assertTrue(result.getCustomerSummaries().isEmpty());
        assertEquals(5, result.getErrors().size());
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid transaction id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid transaction date.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid customer id.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(1).toString() + " : has invalid value.")));
        assertTrue(result.getErrors()
                .stream()
                .anyMatch(e -> e.equals(customerTransaction.getTransactions().get(2).toString() + " : has a Customer id not found in the list of Customers.")));
    }

    @Test
    public void validCustomersAndTransactionsReturnCorrectResults() {
        var customerTransaction = new CustomerTransactionsDTO(
                List.of(
                        new CustomerDTO("100", "Customer 1"),
                        new CustomerDTO("101", "Customer 2"),
                        new CustomerDTO("102", "Customer 3")
                ),
                List.of(
                        new RetailTransactionDTO("1001", "2023-11-03", "100", "120"),
                        new RetailTransactionDTO("1002", "2024-01-31", "101", "70"),
                        new RetailTransactionDTO("1005", "2023-11-01", "102", "50.99"),
                        new RetailTransactionDTO("1006", "2023-11-01", "101", "150.75"),
                        new RetailTransactionDTO("1007", "2023-11-30", "101", "90"),
                        new RetailTransactionDTO("1011", "2023-12-31", "101", "500.50"),
                        new RetailTransactionDTO("1012", "2024-01-31", "100", "65.50")
                )
        );

        var result = service.calculateRewards(customerTransaction);

        assertTrue(result.getErrors().isEmpty());
        assertEquals(3, result.getCustomerSummaries().size());
        var customerSummary = result.getCustomerSummaries().stream()
                .filter(s -> s.getCustomerId() == 100)
                .findFirst();
        assertTrue(customerSummary.isPresent());
        assertEquals(customerTransaction.getCustomers().get(0).getName(), customerSummary.get().getCustomerName());
        assertEquals(90, customerSummary.get().getMonth1Rewards());
        assertEquals(0, customerSummary.get().getMonth2Rewards());
        assertEquals(15, customerSummary.get().getMonth3Rewards());
        assertEquals(105, customerSummary.get().getTotalRewards());

        customerSummary = result.getCustomerSummaries().stream()
                .filter(s -> s.getCustomerId() == 101)
                .findFirst();
        assertTrue(customerSummary.isPresent());
        assertEquals(customerTransaction.getCustomers().get(1).getName(), customerSummary.get().getCustomerName());
        assertEquals(190, customerSummary.get().getMonth1Rewards());
        assertEquals(850, customerSummary.get().getMonth2Rewards());
        assertEquals(20, customerSummary.get().getMonth3Rewards());
        assertEquals(1060, customerSummary.get().getTotalRewards());

        customerSummary = result.getCustomerSummaries().stream()
                .filter(s -> s.getCustomerId() == 102)
                .findFirst();
        assertTrue(customerSummary.isPresent());
        assertEquals(customerTransaction.getCustomers().get(2).getName(), customerSummary.get().getCustomerName());
        assertEquals(0, customerSummary.get().getMonth1Rewards());
        assertEquals(0, customerSummary.get().getMonth2Rewards());
        assertEquals(0, customerSummary.get().getMonth3Rewards());
        assertEquals(0, customerSummary.get().getTotalRewards());
    }
}