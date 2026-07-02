package com.spin.transaction_executor.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class Constants {
    @Value("${business.rules.debit-max-amount}")
    public BigDecimal DEBIT_MAX_AMOUNT;
    @Value("${business.rules.allowed-currencies}")
    public Set<String> ALLOWED_CURRENCIES;

    public static final String REQUEST_ID = "requestId";
    public static final String API_KEY_HEADER = "X-Api-Key";
    public static final String INPUT = "Input: {}";
    public static final String OUTPUT = "Output: {}";
    public static final String EXECUTING = "Executing: {}";
    public static final String ERROR = "Error: {}";
}
