package com.rorysteerprojects.retailrewards.application_services.parsers;

import com.rorysteerprojects.retailrewards.api.RetailTransactionDTO;
import com.rorysteerprojects.retailrewards.config.ResourceLookup;
import com.rorysteerprojects.retailrewards.domain.Customer;
import com.rorysteerprojects.retailrewards.domain.RetailTransaction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class RetailTransactionParser {

    public RetailTransactionParserResult parseTransactions(List<RetailTransactionDTO> transactions,
                                                           List<Customer> customers) {
        if (transactions.isEmpty()) {
            return new RetailTransactionParserResult(Collections.emptyList(), Collections.emptyList());
        }
        List<String> errors = validateTransactions(transactions, customers);
        return errors.isEmpty() ?
                new RetailTransactionParserResult(transactions.stream().map(this::parseTransaction).toList(),
                        Collections.emptyList()) :
                new RetailTransactionParserResult(Collections.emptyList(), errors);
    }

    private List<String> validateTransactions(List<RetailTransactionDTO> transactions, List<Customer> customers) {
        Map<Integer, Customer> customersById = customers.stream()
                .collect(Collectors.toMap(Customer::id, customer -> customer));
        List<String> errors = transactions.stream()
                .map(transaction -> validateTransaction(transaction, customersById))
                .toList()
                .stream()
                .flatMap(Collection::stream)
                .toList();

        return errors.isEmpty() && isTransactionsSpanMoreThanThreeCompleteMonths(transactions) ?
                List.of(ResourceLookup.getMessage("res_transactionsSpanMoreThanThreeMonths")) :
                errors;

    }

    private List<String> validateTransaction(RetailTransactionDTO transaction, Map<Integer, Customer> customersById) {
        List<String> errors = new ArrayList<>();
        for (Map.Entry<Predicate<RetailTransactionDTO>, String> entry : validators.entrySet()) {
            if (entry.getKey().test(transaction)) {
                errors.add(transaction + " : " + ResourceLookup.getMessage(entry.getValue()));
            }
        }
        if (isCustomerIdDoesNotExistInCustomerList(transaction, customersById)) {
            errors.add(transaction + " : " + ResourceLookup.getMessage("res_transactionHasNotFoundCustomerId"));
        }
        return errors;
    }

    private boolean isCustomerIdDoesNotExistInCustomerList(RetailTransactionDTO transaction, Map<Integer, Customer> customersById) {
        return !customersById.isEmpty() &&
                !isInvalidCustomerId.test(transaction) &&
                customersById.get(Integer.parseInt(transaction.getCustomerId())) == null;
    }

    private boolean isTransactionsSpanMoreThanThreeCompleteMonths(List<RetailTransactionDTO> transactions) {
        var transactionWithEarliestDate = transactions.stream()
                .min(Comparator.comparing(
                        transaction -> LocalDate.parse(transaction.getDate())));
        var transactionWithLatestDate = transactions.stream()
                .max(Comparator.comparing(
                        transaction -> LocalDate.parse(transaction.getDate())));

        var earliestDate = LocalDate.parse(transactionWithEarliestDate.get().getDate());
        var latestDate = LocalDate.parse(transactionWithLatestDate.get().getDate());
        var threeMonthSpan = LocalDate.of(earliestDate.getYear(), earliestDate.getMonth(), 1)
                .plusMonths(3)
                .minusDays(1);

        return latestDate.isAfter(threeMonthSpan);
    }

    private RetailTransaction parseTransaction(RetailTransactionDTO transaction) {
        return new RetailTransaction(
                Integer.parseInt(transaction.getId()),
                LocalDate.parse(transaction.getDate()),
                Integer.parseInt(transaction.getCustomerId()),
                Double.parseDouble(transaction.getValue()));
    }

    static Predicate<RetailTransactionDTO> isInvalidTransactionId = transaction -> {
        try {
            Integer.parseInt(transaction.getId());
            return false;
        } catch (Exception e) {
            return true;
        }
    };
    static Predicate<RetailTransactionDTO> isInvalidDate = transaction -> {
        try {
            LocalDate.parse(transaction.getDate());
            return false;
        } catch (Exception e) {
            return true;
        }
    };
    static Predicate<RetailTransactionDTO> isInvalidCustomerId = transaction -> {
        try {
            Integer.parseInt(transaction.getCustomerId());
            return false;
        } catch (Exception e) {
            return true;
        }
    };
    static Predicate<RetailTransactionDTO> isInvalidValue = transaction -> {
        try {
            Double.parseDouble(transaction.getValue());
            return false;
        } catch (Exception e) {
            return true;
        }
    };
    static Predicate<RetailTransactionDTO> isNegativeValue = transaction ->
            !isInvalidValue.test(transaction) && Double.parseDouble(transaction.getValue()) < 0;
    private static final Map<Predicate<RetailTransactionDTO>, String> validators = Map.of(
            isInvalidTransactionId, "res_transactionHasInvalidId",
            isInvalidDate, "res_transactionHasInvalidDate",
            isInvalidCustomerId, "res_transactionHasInvalidCustomerId",
            isInvalidValue, "res_transactionHasInvalidValue",
            isNegativeValue, "res_transactionHasNegativeValue"
    );
}

