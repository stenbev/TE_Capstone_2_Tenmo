package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public AccountService(String baseUrl, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        baseUrl = baseUrl;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0);
        try {
            restTemplate.exchange(baseUrl + "balance/" + currentUser.getUser().getId(), HttpMethod.GET, (HttpEntity<?>) makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Current balance is: " + balance);
        } catch (RestClientException e) {
            System.out.println("Error with getting your balance.");
        }
        return balance;
    }

    private Object makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}