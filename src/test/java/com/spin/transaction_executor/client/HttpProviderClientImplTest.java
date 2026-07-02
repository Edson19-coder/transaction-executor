package com.spin.transaction_executor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spin.transaction_executor.client.impl.HttpProviderClientImpl;
import com.spin.transaction_executor.domain.request.TransactionProviderRequest;
import com.spin.transaction_executor.domain.response.ProviderTransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(HttpProviderClientImpl.class)
@TestPropertySource(properties = {
        "app.config.rest-client.provider.baseUrl=https://api.proveedor.com",
        "app.config.rest-client.provider.endpoints.transaction-execute=/provider/v1/execute"
})
class HttpProviderClientImplTest {
    @Autowired
    private HttpProviderClientImpl httpProviderClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    private TransactionProviderRequest request;
    private ProviderTransactionResponse expectedResponse;
    private final static String transactionExecuteEndpoint = "https://api.proveedor.com/provider/v1/execute";

    @BeforeEach
    void setUp() {
        request = new TransactionProviderRequest();
        expectedResponse = new ProviderTransactionResponse();
    }

    @Test
    void executeTransaction_WhenSuccessful_ShouldReturnResponse() throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = objectMapper.writeValueAsString(expectedResponse);

        mockServer.expect(requestTo(transactionExecuteEndpoint))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestJson))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        ProviderTransactionResponse actualResponse = httpProviderClient.executeTransaction(request);
        assertThat(actualResponse).isNotNull();
        mockServer.verify();
    }

    @Test
    void executeTransaction_WhenHttpErrorWithValidResponseBody_ShouldReturnErrorResponse() throws Exception {
        ProviderTransactionResponse errorResponse = new ProviderTransactionResponse();

        String responseJson = objectMapper.writeValueAsString(errorResponse);

        mockServer.expect(requestTo(transactionExecuteEndpoint))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(responseJson)
                        .contentType(MediaType.APPLICATION_JSON));

        ProviderTransactionResponse actualResponse = httpProviderClient.executeTransaction(request);
        assertThat(actualResponse).isNotNull();
        mockServer.verify();
    }

    @Test
    void executeTransaction_WhenHttpErrorWithEmptyBody_ShouldThrowException() {
        mockServer.expect(requestTo(transactionExecuteEndpoint))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> httpProviderClient.executeTransaction(request))
                .isInstanceOf(HttpServerErrorException.class)
                .hasMessageContaining("500 Internal Server Error");

        mockServer.verify();
    }
}