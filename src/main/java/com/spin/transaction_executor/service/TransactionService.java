package com.spin.transaction_executor.service;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    ResponseEntity<?> sendTransaction(TransactionRequest request);
    ResponseEntity<?> getTransactions(TransactionHistoryRequest request);
}
