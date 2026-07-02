package com.spin.transaction_executor.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spin.transaction_executor.util.CardType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class TransactionResponse {
    private String id;
    private String accountId;
    private CardType type;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String status;
    private String providerTransactionId;
    private BigDecimal balanceAfter;
    private String createdAt;
}
