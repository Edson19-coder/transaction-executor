package com.spin.transaction_executor.repository;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.util.CardType;

import java.util.List;

public interface TransactionRepository {
    boolean saveTransaction(TransactionResponse response);
    List<TransactionResponse> getTransactions(String accountId, String status, CardType type, Integer page, Integer limit);
    Long getTotalTransactions(String accountId, String status, CardType type);
}
