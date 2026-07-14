package com.spin.transaction_executor.service;

import com.spin.transaction_executor.client.HttpProviderClient;
import com.spin.transaction_executor.domain.exception.impl.BusinessRuleException;
import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.ProviderTransactionResponse;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.domain.response.list.TransactionHistoryResponse;
import com.spin.transaction_executor.repository.TransactionRepository;
import com.spin.transaction_executor.service.impl.TransactionServiceImpl;
import com.spin.transaction_executor.util.CardType;
import com.spin.transaction_executor.util.Constants;
import com.spin.transaction_executor.util.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private Constants constants;
    @Mock
    private HttpProviderClient httpProviderClient;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequest validRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(constants, "DEBIT_MAX_AMOUNT", new BigDecimal("10000.00"));
        ReflectionTestUtils.setField(constants, "ALLOWED_CURRENCIES", Set.of("MXN"));

        validRequest = new TransactionRequest();
        validRequest.setAccountId("acc-123456");
        validRequest.setType(CardType.DEBIT);
        validRequest.setAmount(new BigDecimal("1500.00"));
        validRequest.setCurrency("MXN");
        validRequest.setDescription("Transferencia recibida");
    }

    @Test
    void sendTransaction_ShouldFail_WhenDebitAmountExceedsLimit() {
        validRequest.setAmount(new BigDecimal("10001.00"));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            transactionService.sendTransaction(validRequest);
        });

        assertNotNull(exception.getData());
        assertInstanceOf(TransactionResponse.class, exception.getData());
        TransactionResponse failedResponse = (TransactionResponse) exception.getData();
        assertEquals(Status.REJECTED.toString(), failedResponse.getStatus());
        assertEquals("Debit transactions cannot exceed $10,000.00 per transaction", failedResponse.getDescription());
        verify(transactionRepository, times(1)).saveTransaction(any());
        verifyNoInteractions(httpProviderClient);
    }

    @Test
    void sendTransaction_ShouldFail_WhenUnsupportedCurrency() {
        validRequest.setCurrency("USD");

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            transactionService.sendTransaction(validRequest);
        });

        assertNotNull(exception.getData());
        assertInstanceOf(TransactionResponse.class, exception.getData());
        TransactionResponse failedResponse = (TransactionResponse) exception.getData();
        assertEquals(Status.REJECTED.toString(), failedResponse.getStatus());
        assertEquals("Unsupported currency type", failedResponse.getDescription());
        verify(transactionRepository, times(1)).saveTransaction(any());
    }

    @Test
    void sendTransaction_ShouldSucceed_WhenProviderApproves() {
        ProviderTransactionResponse providerResponse = new ProviderTransactionResponse();
        providerResponse.setStatus("APPROVED");
        providerResponse.setTransactionId("txn-"+ ThreadLocalRandom.current().nextInt(1, 10000 + 1));
        providerResponse.setBalance(new BigDecimal("5500.00"));
        providerResponse.setExecutedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());

        when(httpProviderClient.executeTransaction(any())).thenReturn(providerResponse);
        when(transactionRepository.saveTransaction(any())).thenReturn(true);

        TransactionResponse response = transactionService.sendTransaction(validRequest);
        assertEquals(Status.EXECUTED.toString(), response.getStatus());
        verify(transactionRepository, times(1)).saveTransaction(any());
    }

    @Test
    void getTransactions_ShouldReturnEmptyMessage_WhenNoRecordsFound() {
        TransactionHistoryRequest historyRequest = new TransactionHistoryRequest();
        historyRequest.setAccountId("acc-369");
        historyRequest.setPage(1);
        historyRequest.setLimit(10);

        when(transactionRepository.getTotalTransactions(any(), any(), any())).thenReturn(0L);

        TransactionHistoryResponse response = transactionService.getTransactions(historyRequest);
        assertTrue(response.getTransactions().isEmpty());
    }
}