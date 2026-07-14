package com.spin.transaction_executor.service;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.domain.response.list.TransactionHistoryResponse;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    TransactionResponse sendTransaction(TransactionRequest request);
    TransactionHistoryResponse getTransactions(TransactionHistoryRequest request);
}
