package com.spin.transaction_executor.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ProviderTransactionResponse {
    private String transactionId;
    private String status;
    private BigDecimal balance;
    private String code;
    private String message;
    private String executedAt;
}
