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

       assert(result.customers()).isEmpty();
       assert(result.errors()).isEmpty();
    }

    @Test
    public void testOneValidCustomerInListConvertedToCustomerAndNoErrors() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"));
        var result = parser.parserCustomers(customers);

        assert(result.errors()).isEmpty();
        assertEquals(1, result.customers().size());
        assertEquals(101, result.customers().get(0).getId());
        assertEquals("Customer 1", result.customers().get(0).getName());
    }

    @Test
    public void testTwoValidCustomersInListConvertedToCustomersAndNoErrors() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"),
                new CustomerDTO("102", "Customer 2"));
        var result = parser.parserCustomers(customers);

        assert(result.errors()).isEmpty();
        assertEquals(2, result.customers().size());
        var customerResult = result.customers().stream().filter(c -> c.getId() == 101).findFirst();
        assert customerResult.isPresent();
        assertEquals("Customer 1", customerResult.get().getName());
        customerResult = result.customers().stream().filter(c -> c.getId() == 102).findFirst();
        assert customerResult.isPresent();
        assertEquals("Customer 2", customerResult.get().getName());
    }
    @Test
    public void testOneInvalidCustomerInListConvertedToNoCustomersAndOneError() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("badId", "Customer 1"));
        var result = parser.parserCustomers(customers);

        assert(result.customers()).isEmpty();
        assertEquals(1, result.errors().size());
        assertEquals("Invalid customer id for customer name: Customer 1", result.errors().get(0));
    }

    @Test
    public void testMixOfValidAndInvalidCustomersInListConvertedToNoCustomersAndOneError() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"),
                new CustomerDTO("badId", "Customer 2"),
                new CustomerDTO("102", "Customer 3"),
                new CustomerDTO(null, "Customer 4"));
        var result = parser.parserCustomers(customers);

        assert(result.customers()).isEmpty();
        assertEquals(2, result.errors().size());
        assertEquals("Invalid customer id for customer name: Customer 2", result.errors().get(0));
        assertEquals("Invalid customer id for customer name: Customer 4", result.errors().get(1));
    }
    @Test
    public void testAllTypesOfInvalidCustomers() {
        var parser = new CustomerParser();
        var customers = List.of(new CustomerDTO("101", "Customer 1"),
                new CustomerDTO("badId", "Customer 2"),
                new CustomerDTO(null, "Customer 4"),
                new CustomerDTO(null, null));
        var result = parser.parserCustomers(customers);

        assert(result.customers()).isEmpty();
        assertEquals(3, result.errors().size());
        assertEquals("Invalid customer id for customer name: Customer 2", result.errors().get(0));
        assertEquals("Invalid customer id for customer name: Customer 4", result.errors().get(1));
        assertEquals("Invalid customer id for customer name: null", result.errors().get(2));
    }
}