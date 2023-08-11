package com.rorysteerprojects.retailrewards.domain;

import java.time.LocalDate;

public record RetailTransaction(int id, LocalDate date, int customerId, double value) {
}
