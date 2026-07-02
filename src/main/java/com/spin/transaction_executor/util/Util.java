package com.spin.transaction_executor.util;

import com.spin.transaction_executor.domain.request.TransactionHistoryRequest;
import com.spin.transaction_executor.domain.request.TransactionRequest;
import com.spin.transaction_executor.domain.response.ProviderTransactionResponse;
import com.spin.transaction_executor.domain.response.TransactionResponse;
import com.spin.transaction_executor.domain.response.list.TransactionHistoryResponse;
import com.spin.transaction_executor.repository.TransactionRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.xml.crypto.dsig.Transform;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Util {
    public static TransactionResponse fillTransactionResponse(TransactionRequest request, ProviderTransactionResponse providerTransactionResponse) {
        boolean isApproved = Status.APPROVED.name().equals(providerTransactionResponse.getStatus());
        return TransactionResponse.builder()
                .id(UUID.randomUUID().toString())
                .accountId(request.getAccountId())
                .type(request.getType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(!isApproved ? providerTransactionResponse.getMessage() : request.getDescription())
                .status(!isApproved ? providerTransactionResponse.getStatus() : Status.EXECUTED.toString())
                .providerTransactionId(providerTransactionResponse.getTransactionId())
                .balanceAfter(providerTransactionResponse.getBalance())
                .createdAt(isApproved ? Instant.parse(providerTransactionResponse.getExecutedAt()).truncatedTo(ChronoUnit.SECONDS).toString() : Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .build();
    }

    public static TransactionResponse fillFailedTransactionResponse(TransactionRequest request, String description) {
        return TransactionResponse.builder()
                .id(UUID.randomUUID().toString())
                .accountId(request.getAccountId())
                .type(request.getType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(description)
                .status(Status.REJECTED.toString())
                .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .build();
    }

    public static boolean validTransactionHistoryRequest(TransactionHistoryRequest request) {
        return (StringUtils.isNotBlank(request.getAccountId()) || StringUtils.isNotBlank(request.getStatus()) || StringUtils.isNotBlank(request.getType().toString()) || request.getPage() > 0 || request.getLimit() > 0);
    }

    public static TransactionHistoryResponse fillTransactionHistoryResponse(List<TransactionResponse> transactions, Long totalTrx, Integer page, Integer limit) {
        int totalPages = (int) Math.ceil((double) totalTrx / limit);
        if (totalPages == 0) totalPages = 1;

        return TransactionHistoryResponse.builder()
                .transactions(transactions)
                .currentPage(page)
                .totalPages(totalPages)
                .totalTransactions(totalTrx)
                .build();
    }

    // Función para poder guardar el resultado de la transacción (sea fallida o satisfactoria)
    public static void saveTransaction(TransactionResponse response, TransactionRepository transactionRepository) {
        if (response == null) {
            log.warn("Attempted to save a null transaction response");
            return;
        }

        try {
            if (!transactionRepository.saveTransaction(response)) {
                log.warn("Error saving transaction information in database");
            }
        } catch (Exception databaseException) {
            log.error("Database failure while saving transaction logs: {}", databaseException.getMessage());
        }
    }
}
