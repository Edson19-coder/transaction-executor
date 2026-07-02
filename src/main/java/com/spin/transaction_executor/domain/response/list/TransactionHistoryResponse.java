package com.spin.transaction_executor.domain.response.list;

import com.spin.transaction_executor.domain.response.TransactionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TransactionHistoryResponse {
    List<TransactionResponse> transactions;
    Integer currentPage;
    Integer totalPages;
    Long totalTransactions;
}
