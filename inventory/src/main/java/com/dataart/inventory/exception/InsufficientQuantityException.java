package com.dataart.inventory.exception;

import com.dataart.common.exception.BusinessException;

public class InsufficientQuantityException extends BusinessException {

    public InsufficientQuantityException(final String transactionId, final Long productId) {
        super(transactionId, String.format("Insufficient quantity for productId: %d", productId));
    }
}
