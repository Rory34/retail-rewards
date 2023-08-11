package com.rorysteerprojects.retailrewards.application_services.parsers;

import com.rorysteerprojects.retailrewards.api.CustomerDTO;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerParserTest {

    @Test
    public void testEmptyListReturnsEmptyList() {
       var parser = new CustomerParser();
       var result = parser.parserCustomers(Collections.emptyList());

       assertTrue(result.customers().isEmpty());
       assertTrue(result.errors().isEmpty());
    }

    @Test
    public void testOneValidCustomerInListConvertedToCustomerAndNoErrors() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"));
        var result = parser.parserCustomers(customers);

        assertTrue(result.errors().isEmpty());
        assertEquals(1, result.customers().size());
        assertEquals(101, result.customers().get(0).id());
        assertEquals("Customer 1", result.customers().get(0).name());
    }

    @Test
    public void testTwoValidCustomersInListConvertedToCustomersAndNoErrors() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"),
                new CustomerDTO("102", "Customer 2"));
        var result = parser.parserCustomers(customers);

        assertTrue(result.errors().isEmpty());
        assertEquals(2, result.customers().size());
        var customerResult = result.customers().stream().filter(c -> c.id() == 101).findFirst();
        assertTrue(customerResult.isPresent());
        assertEquals("Customer 1", customerResult.get().name());
        customerResult = result.customers().stream().filter(c -> c.id() == 102).findFirst();
        assertTrue(customerResult.isPresent());
        assertEquals("Customer 2", customerResult.get().name());
    }
    @Test
    public void testOneInvalidCustomerInListConvertedToNoCustomersAndOneError() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("badId", "Customer 1"));
        var result = parser.parserCustomers(customers);

        assertTrue(result.customers().isEmpty());
        assertEquals(1, result.errors().size());
        assertEquals(customers.get(0).toString() + " : has an invalid customer id.", result.errors().get(0));
    }

    @Test
    public void testMixOfValidAndInvalidCustomersInListConvertedToNoCustomersAndOneError() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"),
                new CustomerDTO("badId", "Customer 2"),
                new CustomerDTO("102", "Customer 3"),
                new CustomerDTO(null, "Customer 4"));
        var result = parser.parserCustomers(customers);

        assertTrue(result.customers().isEmpty());
        assertEquals(2, result.errors().size());
        assertEquals(customers.get(1).toString() + " : has an invalid customer id.", result.errors().get(0));
        assertEquals(customers.get(3).toString() + " : has an invalid customer id.", result.errors().get(1));
    }
    @Test
    public void testAllTypesOfInvalidCustomers() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"),
                new CustomerDTO("badId", "Customer 2"),
                new CustomerDTO(null, "Customer 4"),
                new CustomerDTO(null, null));
        var result = parser.parserCustomers(customers);

        assertTrue(result.customers().isEmpty());
        assertEquals(3, result.errors().size());
        assertTrue(result.errors()
                .stream()
                .anyMatch(e -> e.equals(customers.get(1).toString() + " : has an invalid customer id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(e -> e.equals(customers.get(2).toString() + " : has an invalid customer id.")));
        assertTrue(result.errors()
                .stream()
                .anyMatch(e -> e.equals(customers.get(3).toString() + " : has an invalid customer id.")));
    }
}