package com.spin.transaction_executor.client.impl;

import com.spin.transaction_executor.client.HttpProviderClient;
import com.spin.transaction_executor.domain.request.TransactionProviderRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.ProviderTransactionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Component
public class HttpProviderClientImpl implements HttpProviderClient {
    private final String providerTransactionExecute;
    private final RestClient restClient;

    public HttpProviderClientImpl(
        RestClient.Builder restClientBuilder,
        @Value("${app.config.rest-client.provider.baseUrl}") String providerBaseUrl,
        @Value("${app.config.rest-client.provider.endpoints.transaction-execute}") String providerTransactionExecute
    ) {
        this.providerTransactionExecute = providerTransactionExecute;
        this.restClient = restClientBuilder
                .baseUrl(providerBaseUrl)
                .build();
    }

    @Override
    @Retry(name = "provider")
    @CircuitBreaker(name = "provider")
    public ProviderTransactionResponse executeTransaction(TransactionProviderRequest request) {
        try {
            return restClient.post()
                    .uri(providerTransactionExecute)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ProviderTransactionResponse.class);
        } catch (HttpStatusCodeException ex) {
            ProviderTransactionResponse errorResponse = ex.getResponseBodyAs(ProviderTransactionResponse.class);
            if (errorResponse != null) {
                return errorResponse;
            }
            throw ex;
        }
    }
}
