package com.rorysteerprojects.retailrewards.domain;

public record RewardsResult(int customerId, int[] monthlyTotals, int threeMonthTotal) {
}
