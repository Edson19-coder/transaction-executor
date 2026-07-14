package com.spin.transaction_executor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.service.TransactionService;
import com.spin.transaction_executor.util.CardType;
import com.spin.transaction_executor.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@TestPropertySource(properties = "app.security.api-key=test-api-key")
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TransactionService transactionService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID_API_KEY = "test-api-key";
    private static final String TRANSACTION_MAPPING = "/transactions";

    @Test
    void sendTransaction_WithValidRequest_ShouldReturnOk() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAccountId("acc-123456");
        request.setType(CardType.CREDIT);
        request.setAmount(new BigDecimal("1500.00"));
        request.setCurrency("MXN");
        request.setDescription("Transferencia recibida");

        when(transactionService.sendTransaction(any())).thenReturn(any());

        mockMvc.perform(post(TRANSACTION_MAPPING)
                        .header(Constants.API_KEY_HEADER, VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void sendTransaction_WithInvalidApiKey_ShouldReturnUnauthorized() throws Exception {
        TransactionRequest request = new TransactionRequest();
        mockMvc.perform(post(TRANSACTION_MAPPING)
                        .header(Constants.API_KEY_HEADER, "api-key-incorrecta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()) // Esperamos un 401
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid or missing API key"));
    }

    @Test
    void sendTransaction_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        TransactionRequest invalidRequest = new TransactionRequest();
        mockMvc.perform(post(TRANSACTION_MAPPING)
                        .header(Constants.API_KEY_HEADER, VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
