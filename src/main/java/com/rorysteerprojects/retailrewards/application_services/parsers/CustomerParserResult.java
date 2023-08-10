package com.rorysteerprojects.retailrewards.application_services.parsers;

import com.rorysteerprojects.retailrewards.domain.Customer;

import java.util.List;

public record CustomerParserResult(List<Customer> customers, List<String> errors) {}
