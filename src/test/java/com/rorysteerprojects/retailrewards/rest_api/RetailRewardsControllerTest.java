package com.rorysteerprojects.retailrewards.rest_api;

import com.rorysteerprojects.retailrewards.api.*;
import com.rorysteerprojects.retailrewards.application_services.RetailRewardsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetailRewardsControllerTest {

    @InjectMocks
    RetailRewardsController retailRewardsController;

    @Mock
    RetailRewardsService retailRewardsService;

    @Test
    public void testNoRequestBody() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(null);
        assertEquals(400, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getErrors());
        assertEquals(1, responseEntity.getBody().getErrors().size());
        assertTrue(responseEntity.getBody().getErrors()
                .stream()
                .anyMatch(e -> e.equals("Request Body must contain both a customer list and a transaction list")));
    }
    @Test
    public void testNoCustomerList() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(null, Collections.emptyList()));

        assertEquals(400, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getErrors());
        assertEquals(1, responseEntity.getBody().getErrors().size());
        assertTrue(responseEntity.getBody().getErrors()
                .stream()
                .anyMatch(e -> e.equals("Request Body must contain both a customer list and a transaction list")));

    }
    @Test
    public void testNoTransactionsList() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(Collections.emptyList(), null));

        assertEquals(400, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getErrors());
        assertEquals(1, responseEntity.getBody().getErrors().size());
        assertTrue(responseEntity.getBody().getErrors()
                .stream()
                .anyMatch(e -> e.equals("Request Body must contain both a customer list and a transaction list")));

    }

    @Test
    public void testTransactionsListContainsElementsButCustomersIsEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(Collections.emptyList(), List.of(new RetailTransactionDTO("1", "2023-08-09", "0", "0")))
        );

        assertEquals(400, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getErrors());
        assertEquals(1, responseEntity.getBody().getErrors().size());
        assertTrue(responseEntity.getBody().getErrors()
                .stream()
                .anyMatch(e -> e.equals("Request Body must contain both a customer list and a transaction list")));
    }

    @Test
    public void testParsingErrorsReturn422() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        var errorMessage = "Invalid Transaction";
        when(retailRewardsService.calculateRewards(any(CustomerTransactionsDTO.class)))
                .thenReturn(new RewardsResultDTO(Collections.emptyList(), List.of(errorMessage)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(List.of(new CustomerDTO("1", "name")), List.of(new RetailTransactionDTO("1", "2023-08-09", "0", "0")))
        );

        assertEquals(422, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().getCustomerSummaries().isEmpty());
        assertNotNull(responseEntity.getBody().getErrors());
        assertEquals(1, responseEntity.getBody().getErrors().size());
        assertTrue(responseEntity.getBody().getErrors()
                .stream()
                .anyMatch(e -> e.equals(errorMessage)));
    }

    @Test
    public void testSuccessfulReturn() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        var customerSummary = new CustomerSummaryDTO(1, "Cust1", 0, 0, 0, 0);
        when(retailRewardsService.calculateRewards(any(CustomerTransactionsDTO.class)))
                .thenReturn(new RewardsResultDTO(List.of(customerSummary), Collections.emptyList()));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(List.of(new CustomerDTO("1", "name")), List.of(new RetailTransactionDTO("1", "2023-08-09", "0", "0")))
        );

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getCustomerSummaries().size());
        assertTrue(responseEntity.getBody().getCustomerSummaries()
                .stream()
                .anyMatch(cs -> cs.getCustomerId() == customerSummary.getCustomerId()));
        assertTrue(responseEntity.getBody().getErrors().isEmpty());
    }
}