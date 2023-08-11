package com.rorysteerprojects.retailrewards.application_services.parsers;

import com.rorysteerprojects.retailrewards.domain.RetailTransaction;

import java.util.List;

public record RetailTransactionParserResult(List<RetailTransaction> retailTransactions, List<String> errors) {}
