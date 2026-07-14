package com.spin.transaction_executor.domain.exception.impl;

import com.spin.transaction_executor.domain.exception.GlobalException;

public class FatalException extends RuntimeException implements GlobalException {
    private final Object data;

    public FatalException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public FatalException(String message) {
        super(message);
        this.data = null;
    }

    public Object getData() {
        return this.data;
    }
}
