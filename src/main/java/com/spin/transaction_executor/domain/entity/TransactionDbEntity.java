package com.spin.transaction_executor.domain.entity;

import com.spin.transaction_executor.util.CardType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class TransactionDbEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private CardType type;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PROVIDER_TRANSACTION_ID")
    private String providerTransactionId;

    @Column(name = "BALANCE_AFTER")
    private BigDecimal balanceAfter;

    @Column(name = "CREATED_AT")
    private String createdAt;
}
