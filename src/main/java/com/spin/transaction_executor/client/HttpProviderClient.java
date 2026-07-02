package com.spin.transaction_executor.client;

import com.spin.transaction_executor.domain.request.TransactionProviderRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.ProviderTransactionResponse;

public interface HttpProviderClient {
    public ProviderTransactionResponse executeTransaction(TransactionProviderRequest request);
}
