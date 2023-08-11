package com.rorysteerprojects.retailrewards.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RewardsCalculator {

    private static final int THRESHOLD_FOR_SINGLE_POINTS = 50;
    private static final int THRESHOLD_FOR_DOUBLE_POINTS = 100;
    public List<RewardsResult> calculate(List<RetailTransaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }
        var secondMonthStartDate = calculateTheDateOfTheFirstDayOfTheSecondMonth(transactions);
        var thirdMonthStartDate = secondMonthStartDate.plusMonths(1);


        Map<Integer, int[]> rewardsByCustomerId = transactions.stream()
                .collect(Collectors.toMap(RetailTransaction::customerId,
                        transaction -> getRewardsForTransaction(transaction, secondMonthStartDate, thirdMonthStartDate),
                        this::combineRewardsArrays
                ));

        return rewardsByCustomerId.entrySet()
                .stream()
                .map(entrySet -> new RewardsResult(entrySet.getKey(),
                        entrySet.getValue(),
                        IntStream.of(entrySet.getValue()).sum()))
                .toList();
        //  Authors Note:  I realize I can combine the two statements above. I chose not to do so deliberately for
        //   readability and maintainability.  I favor easy to read code versus train wrecks of cleverness.
    }

    private LocalDate calculateTheDateOfTheFirstDayOfTheSecondMonth(List<RetailTransaction> transactions) {
        var transactionWithEarliestDate = transactions.stream().min(Comparator.comparing(RetailTransaction::date));
        return LocalDate.of(transactionWithEarliestDate.get().date().getYear(),
                transactionWithEarliestDate.get().date().getMonth(),
                1).plusMonths(1);
    }

    private int[] getRewardsForTransaction(RetailTransaction transaction,
                                           LocalDate secondMonthStartDate,
                                           LocalDate thirdMonthStartDate) {

        var monthNumber = transaction.date().isBefore(secondMonthStartDate) ? 0 :
                transaction.date().isBefore(thirdMonthStartDate) ? 1 :
                        2;

        int[] rewards = {0, 0, 0};
        rewards[monthNumber] = getRewardsPoints(transaction);
        return rewards;
    }

    private int getRewardsPoints(RetailTransaction transaction) {
        if (transaction.value() < THRESHOLD_FOR_SINGLE_POINTS) {
            return 0;
        }

        var wholeDollars = (int) transaction.value();
        var calculator = transaction.value() > THRESHOLD_FOR_DOUBLE_POINTS ?
                calculateDoublePoints : calculateSinglePoints;
        return calculator.apply(wholeDollars);
    }

    UnaryOperator<Integer> calculateDoublePoints = wholeDollars ->
            wholeDollars * 2 - THRESHOLD_FOR_DOUBLE_POINTS - THRESHOLD_FOR_SINGLE_POINTS;

    UnaryOperator<Integer>  calculateSinglePoints = wholeDollars -> wholeDollars - THRESHOLD_FOR_SINGLE_POINTS;

    private int[] combineRewardsArrays(int[] existingRewards, int[] rewardsToAdd) {
        int[] rewardsTotal = new int[]{0, 0, 0};
        for (int i = 0; i < rewardsTotal.length; i++) {
            rewardsTotal[i] = existingRewards[i] + rewardsToAdd[i];
        }
        return rewardsTotal;
    }
}
