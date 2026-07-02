package com.spin.transaction_executor.repository;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.response.TransactionResponse;

import java.util.List;

public interface TransactionRepository {
    boolean saveTransaction(TransactionResponse response);
    List<TransactionResponse> getTransactions(TransactionHistoryRequest request);
    Long getTotalTransactions(TransactionHistoryRequest request);
}
