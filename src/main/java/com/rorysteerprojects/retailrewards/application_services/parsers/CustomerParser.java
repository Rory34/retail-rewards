package com.rorysteerprojects.retailrewards.application_services.parsers;

import com.rorysteerprojects.retailrewards.api.CustomerDTO;
import com.rorysteerprojects.retailrewards.config.ResourceLookup;
import com.rorysteerprojects.retailrewards.domain.Customer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomerParser {
    public CustomerParserResult parserCustomers(List<CustomerDTO> customers) {
        if (customers.isEmpty()) {
            return new CustomerParserResult(Collections.emptyList(), Collections.emptyList());
        }

        List<String> errors = customers.stream()
                .filter(this::isCustomerIdInvalid)
                .map(customer -> ResourceLookup.getMessage("res_invalidCustomerId") + customer.getName())
                .toList();


        return errors.isEmpty() ?
                new CustomerParserResult(convertCustomers(customers), Collections.emptyList()) :
                new CustomerParserResult(Collections.emptyList(), errors);
    }

    private boolean isCustomerIdInvalid(CustomerDTO customer) {
        try {
            Integer.parseInt(customer.getId());
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }

    private List<Customer> convertCustomers(List<CustomerDTO> customers) {
        return customers.stream()
                .map(customer -> new Customer(Integer.parseInt(customer.getId()),
                        customer.getName()))
                .toList();
    }
}

