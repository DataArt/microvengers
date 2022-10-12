package com.dataart.common.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

    private String transactionId;

    public BusinessException(String transactionId, String message) {
        super(message);
        this.transactionId = transactionId;
    }

    public BusinessException(String message) {
        super(message);
    }
}
