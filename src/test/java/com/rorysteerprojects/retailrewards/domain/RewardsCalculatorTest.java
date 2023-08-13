package com.rorysteerprojects.retailrewards.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RewardsCalculatorTest {

    private final RewardsCalculator rewardsCalculator = new RewardsCalculator();

    @Test
    public void oneTransactionReturnsTheRewardsResultForCustomer() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 6, 3), 100, 10));

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(1, result.size());
        var rewardResult = result.get(0);
        assertEquals(transactions.get(0).customerId(), rewardResult.customerId());
        assertEquals(0, rewardResult.monthlyTotals()[0]);
        assertEquals(0, rewardResult.monthlyTotals()[1]);
        assertEquals(0, rewardResult.monthlyTotals()[2]);
        assertEquals(0, rewardResult.threeMonthTotal());
    }

    @Test
    public void oneTransactionReturnsTheRewardsResultForCustomerAndCalculatesTheRewardsAbove50InTheFirstMonth() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 6, 3), 100, 60));

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(1, result.size());
        var rewardResult = result.get(0);
        assertEquals(transactions.get(0).customerId(), rewardResult.customerId());
        assertEquals(10, rewardResult.monthlyTotals()[0]);
        assertEquals(0, rewardResult.monthlyTotals()[1]);
        assertEquals(0, rewardResult.monthlyTotals()[2]);
        assertEquals(10, rewardResult.threeMonthTotal());
    }

    @Test
    public void oneTransactionReturnsTheRewardsResultForCustomerAndCalculatesTheRewardsAbove100InTheFirstMonth() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 6, 3), 100, 120));

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(1, result.size());
        var rewardResult = result.get(0);
        assertEquals(transactions.get(0).customerId(), rewardResult.customerId());
        assertEquals(90, rewardResult.monthlyTotals()[0]);
        assertEquals(0, rewardResult.monthlyTotals()[1]);
        assertEquals(0, rewardResult.monthlyTotals()[2]);
        assertEquals(90, rewardResult.threeMonthTotal());
    }
    @Test
    public void twoTransactionsForSameCustomerDifferentMonths() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 6, 3), 100, 120),
                new RetailTransaction(1002, LocalDate.of(2023, 7, 23), 100, 70)
                );

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(1, result.size());
        var rewardResult = result.get(0);
        assertEquals(transactions.get(0).customerId(), rewardResult.customerId());
        assertEquals(90, rewardResult.monthlyTotals()[0]);
        assertEquals(20, rewardResult.monthlyTotals()[1]);
        assertEquals(0, rewardResult.monthlyTotals()[2]);
        assertEquals(110, rewardResult.threeMonthTotal());
    }
    @Test
    public void twoTransactionsForDifferentCustomersDifferentMonths() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 6, 3), 100, 120),
                new RetailTransaction(1002, LocalDate.of(2023, 8, 31), 101, 70)
        );

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(2, result.size());
        var rewardsResult = result.stream().filter(r -> r.customerId() == 100).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(90, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(90, rewardsResult.get().threeMonthTotal());

        rewardsResult = result.stream().filter(r -> r.customerId() == 101).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(0, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(20, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(20, rewardsResult.get().threeMonthTotal());
    }

    @Test
    public void multipleTransactionsForDifferentCustomersDifferentMonthsAndYears() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 11, 3), 100, 120),
                new RetailTransaction(1002, LocalDate.of(2024, 1, 31), 101, 70),
                new RetailTransaction(1005, LocalDate.of(2023, 11, 1), 102, 50.99),
                new RetailTransaction(1006, LocalDate.of(2023, 11, 1), 101, 150.75),
                new RetailTransaction(1007, LocalDate.of(2023, 11, 30), 101, 90),
                new RetailTransaction(1011, LocalDate.of(2023, 12, 31), 101, 500.50),
                new RetailTransaction(1012, LocalDate.of(2024, 1, 31), 100, 65.50)
        );

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(3, result.size());
        var rewardsResult = result.stream().filter(r -> r.customerId() == 100).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(90, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(15, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(105, rewardsResult.get().threeMonthTotal());

        rewardsResult = result.stream().filter(r -> r.customerId() == 101).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(190, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(850, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(20, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(1060, rewardsResult.get().threeMonthTotal());

        rewardsResult = result.stream().filter(r -> r.customerId() == 102).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(0, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(0, rewardsResult.get().threeMonthTotal());
    }

    @Test
    public void tesThresholds() {
        var transactions = List.of(
                new RetailTransaction(1001, LocalDate.of(2023, 6, 3), 100, 50.99),
                new RetailTransaction(1002, LocalDate.of(2023, 6, 30), 101, 51),
                new RetailTransaction(1003, LocalDate.of(2023, 6, 30), 102, 100.99),
                new RetailTransaction(1004, LocalDate.of(2023, 6, 30), 103, 101)
        );

        var result = rewardsCalculator.calculate(transactions);

        assertEquals(4, result.size());
        var rewardsResult = result.stream().filter(r -> r.customerId() == 100).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(0, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(0, rewardsResult.get().threeMonthTotal());

        rewardsResult = result.stream().filter(r -> r.customerId() == 101).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(1, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(1, rewardsResult.get().threeMonthTotal());

        rewardsResult = result.stream().filter(r -> r.customerId() == 102).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(50, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(50, rewardsResult.get().threeMonthTotal());

        rewardsResult = result.stream().filter(r -> r.customerId() == 103).findFirst();
        assertTrue(rewardsResult.isPresent());
        assertEquals(52, rewardsResult.get().monthlyTotals()[0]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[1]);
        assertEquals(0, rewardsResult.get().monthlyTotals()[2]);
        assertEquals(52, rewardsResult.get().threeMonthTotal());
    }

}