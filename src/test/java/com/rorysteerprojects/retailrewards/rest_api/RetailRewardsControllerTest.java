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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetailRewardsControllerTest {

    @InjectMocks
    RetailRewardsController retailRewardsController;

    @Mock
    RetailRewardsService retailRewardsService;

    @Test
    public void testNoBody() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(null);
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(400);
        assertThat(responseEntity.getBody().getErrors().get(0)).isEqualTo("Request Body must contain both a customer list and a transaction list");

    }
    @Test
    public void testNoCustomerList() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(null, Collections.emptyList()));

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(400);
        assertThat(responseEntity.getBody().getErrors().get(0)).isEqualTo("Request Body must contain both a customer list and a transaction list");

    }
    @Test
    public void testNoTransactionsList() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(Collections.emptyList(), null));

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(400);
        assertThat(responseEntity.getBody().getErrors().get(0)).isEqualTo("Request Body must contain both a customer list and a transaction list");

    }

    @Test
    public void testTransactionsListContainsElementsButCustomersIsEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((request)));

        ResponseEntity<RewardsResultDTO> responseEntity = retailRewardsController.calculateRewards(
                new CustomerTransactionsDTO(Collections.emptyList(), List.of(new RetailTransactionDTO("1", "2023-08-09", "0", "0")))
        );

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(400);

        assertThat(responseEntity.getBody().getErrors().get(0)).isEqualTo("Request Body must contain both a customer list and a transaction list");
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

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(422);
        assert(responseEntity.getBody().getCustomerSummaries().isEmpty());
        assertThat(responseEntity.getBody().getErrors().get(0)).isEqualTo(errorMessage);
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

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody().getCustomerSummaries().get(0)).isEqualTo(customerSummary);
        assert(responseEntity.getBody().getErrors().isEmpty());
    }
}