package com.spin.transaction_executor.domain.exception.impl;

import com.spin.transaction_executor.domain.exception.GlobalException;

public class ProviderCommunicationException extends RuntimeException implements GlobalException {
    private final Object data;

    public ProviderCommunicationException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public ProviderCommunicationException(String message) {
        super(message);
        this.data = null;
    }

    public Object getData() {
        return this.data;
    }
}
