package com.rorysteerprojects.retailrewards.application_services.parsers;

import com.rorysteerprojects.retailrewards.api.RetailTransactionDTO;
import com.rorysteerprojects.retailrewards.domain.Customer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RetailTransactionParserTest {

    private RetailTransactionParser parser = new RetailTransactionParser();

    @Test
    public void testEmptyListReturnsEmptyList() {
        var result = parser.parseTransactions(Collections.emptyList(), Collections.emptyList());
        assertTrue(result.retailTransactions().isEmpty());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    public void testOneTransactionWithInvalidIdInListIsConvertedToErrors() {
        var transactions = List.of(new RetailTransactionDTO("badId", "2023-08-09", "1", "150.23"));
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid transaction id.")));
    }

    @Test
    public void testOneTransactionWithInvalidDateInListIsConvertedToErrors() {
        var transactions = List.of(new RetailTransactionDTO("1001", "badDate", "1", "150.23"));
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid transaction date.")));
    }

    @Test
    public void testOneTransactionWithInvalidCustomerIdInListIsConvertedToErrors() {
        var transactions = List.of(new RetailTransactionDTO("1001", "2023-08-09", "badId", "150.23"));
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid customer id.")));
    }

    @Test
    public void testOneTransactionWithInvalidValueInListIsConvertedToErrors() {
        var transactions = List.of(new RetailTransactionDTO("1001", "2023-08-09", "1", "badValue"));
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid value.")));
    }

    @Test
    public void testTransactionsWithNegativeAndOtherInvalidValuesInListIsConvertedToErrors() {
        var transactions = List.of(
                new RetailTransactionDTO("1001", "2023-08-09", "1", "-50.2"),
                new RetailTransactionDTO("1001", "2023-08-09", "1", "10.5-"),
                new RetailTransactionDTO("1001", "2023-08-09", "1", "1,010.5"));
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(3, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has a negative value.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(1).toString() + " : has invalid value.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(2).toString() + " : has invalid value.")));
    }
    @Test
    public void testTransactionsWithCustomerIdNotInCustomerListIsConvertedToErrors() {
        var transactions = List.of(
                new RetailTransactionDTO("1001", "2023-08-09", "1", "50.2"),
                new RetailTransactionDTO("1001", "2023-08-09", "2", "10.5"));
        var customers = List.of(new Customer(1, "Customer 1"));
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(1, result.errors().size());
        assertEquals(transactions.get(1).toString() + " : has a Customer id not found in the list of Customers.",
                result.errors().get(0));
    }


    @Test
    public void testOneValidTransactionInListReturnsNoErrors() {
        var transactions = List.of(new RetailTransactionDTO("1001", "2023-08-09", "1", "150.53"));
        List<Customer> customers = List.of(new Customer(1, "Customer 1"));
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.errors().isEmpty());
    }

    @Test
    public void testOneTransactionInListWithAllValuesInvalidIsConvertedToErrors() {
        var transactions = List.of(new RetailTransactionDTO("badId", "badDate", "badCustomerId", "badValue"));
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(4, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid transaction id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid transaction date.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid customer id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid value.")));
    }

    @Test
    public void testTwoTransactionsInListWithAllValuesInvalidIsConvertedToErrors() {
        var transactions = List.of(
                new RetailTransactionDTO("badId", "badDate", "badCustomerId", "badValue"),
                new RetailTransactionDTO(null, null, null, null)
        );
        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(8, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid transaction id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid transaction date.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid customer id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(0).toString() + " : has invalid value.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(1).toString() + " : has invalid transaction id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(1).toString() + " : has invalid transaction date.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(1).toString() + " : has invalid customer id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(t -> t.equals(transactions.get(1).toString() + " : has invalid value.")));
    }

    @Test
    public void testManyTransactionsInListWithAllValuesInvalidIsConvertedToErrors() {
        var transactions = List.of(
                new RetailTransactionDTO("badId", "badDate", "badCustomerId", "badValue"),
                new RetailTransactionDTO(null, null, null, null),
                new RetailTransactionDTO("badId", "badDate", "badCustomerId", "badValue"),
                new RetailTransactionDTO("badId", "badDate", "badCustomerId", "badValue"),
                new RetailTransactionDTO(null, null, null, null)
        );

        List<Customer> customers = Collections.emptyList();
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(20, result.errors().size());
    }

    @Test
    public void testOneValidTransactionInListReturnsCorrectTransactionsAndNoErrors() {
        var transactions = List.of(new RetailTransactionDTO("1001", "2023-08-09", "101", "150.53"));
        List<Customer> customers = List.of(new Customer(101, "Customer 1"));
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.errors().isEmpty());
        assertFalse(result.retailTransactions().isEmpty());
        var resultTransaction = result.retailTransactions()
                .stream()
                .filter(t -> t.id() == Integer.parseInt(transactions.get(0).getId()))
                .findFirst();
        assertTrue(resultTransaction.isPresent());
        assertEquals(LocalDate.parse(transactions.get(0).getDate()), resultTransaction.get().date());
        assertEquals(Integer.parseInt(transactions.get(0).getCustomerId()), resultTransaction.get().customerId());
        assertEquals(Double.parseDouble(transactions.get(0).getValue()), resultTransaction.get().value());
    }

    @Test
    public void testMultipleValidTransactionInListReturnsCorrectTransactionsAndNoErrors() {
        var transactions = List.of(
                new RetailTransactionDTO("1001", "2023-08-09", "101", "150.53"),
                new RetailTransactionDTO("1002", "2023-08-10", "102", "160"),
                new RetailTransactionDTO("1003", "2023-08-11", "103", "17.1"),
                new RetailTransactionDTO("1010", "2023-08-11", "101", "1000"));
        List<Customer> customers = List.of(new Customer(101, "Customer 1"),
                new Customer(102, "Customer 2"),
                new Customer(103, "Customer 3"));
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.errors().isEmpty());
        assertEquals(transactions.size(), result.retailTransactions().size());
        var resultTransaction = result.retailTransactions()
                .stream()
                .filter(t -> t.id() == Integer.parseInt(transactions.get(0).getId()))
                .findFirst();
        assertTrue(resultTransaction.isPresent());
        assertEquals(LocalDate.parse(transactions.get(0).getDate()), resultTransaction.get().date());
        assertEquals(Integer.parseInt(transactions.get(0).getCustomerId()), resultTransaction.get().customerId());
        assertEquals(Double.parseDouble(transactions.get(0).getValue()), resultTransaction.get().value());
        resultTransaction = result.retailTransactions()
                .stream()
                .filter(t -> t.id() == Integer.parseInt(transactions.get(1).getId()))
                .findFirst();
        assertTrue(resultTransaction.isPresent());
        assertEquals(LocalDate.parse(transactions.get(1).getDate()), resultTransaction.get().date());
        assertEquals(Integer.parseInt(transactions.get(1).getCustomerId()), resultTransaction.get().customerId());
        assertEquals(Double.parseDouble(transactions.get(1).getValue()), resultTransaction.get().value());
        resultTransaction = result.retailTransactions()
                .stream()
                .filter(t -> t.id() == Integer.parseInt(transactions.get(2).getId()))
                .findFirst();
        assertTrue(resultTransaction.isPresent());
        assertEquals(LocalDate.parse(transactions.get(2).getDate()), resultTransaction.get().date());
        assertEquals(Integer.parseInt(transactions.get(2).getCustomerId()), resultTransaction.get().customerId());
        assertEquals(Double.parseDouble(transactions.get(2).getValue()), resultTransaction.get().value());
        resultTransaction = result.retailTransactions()
                .stream()
                .filter(t -> t.id() == Integer.parseInt(transactions.get(3).getId()))
                .findFirst();
        assertTrue(resultTransaction.isPresent());
        assertEquals(LocalDate.parse(transactions.get(3).getDate()), resultTransaction.get().date());
        assertEquals(Integer.parseInt(transactions.get(3).getCustomerId()), resultTransaction.get().customerId());
        assertEquals(Double.parseDouble(transactions.get(3).getValue()), resultTransaction.get().value());
    }

    @Test
    public void testTransactionsSpanningMoreThanThreeCompleteMonthsReturnsAnError() {
        var transactions = List.of(
                new RetailTransactionDTO("1001", "2022-08-09", "101", "150.53"),
                new RetailTransactionDTO("1002", "2023-08-10", "102", "160"),
                new RetailTransactionDTO("1003", "2023-08-11", "103", "17.1"),
                new RetailTransactionDTO("1010", "2023-08-11", "101", "0"));
        List<Customer> customers = List.of(
                new Customer(101, "Customer 1"),
                new Customer(102, "Customer 2"),
                new Customer(103, "Customer 3"));
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.retailTransactions().isEmpty());
        assertEquals(1, result.errors().size());
        assertEquals("Transaction details contain more than 3 calendar Months of transactions.",
                result.errors().get(0));
    }
    @Test
    public void testTransactionsSpanningExactlyThreeCompleteMonthsDoesNotReturnAnError() {
        var transactions = List.of(
                new RetailTransactionDTO("1001", "2023-06-01", "101", "150.53"),
                new RetailTransactionDTO("1002", "2023-08-10", "102", "160"),
                new RetailTransactionDTO("1003", "2023-08-11", "103", "17.1"),
                new RetailTransactionDTO("1010", "2023-08-31", "101", "0"));
        List<Customer> customers = List.of(
                new Customer(101, "Customer 1"),
                new Customer(102, "Customer 2"),
                new Customer(103, "Customer 3"));
        var result = parser.parseTransactions(transactions, customers);

        assertTrue(result.errors().isEmpty());
        assertEquals(4, result.retailTransactions().size());
    }
}