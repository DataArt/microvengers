package com.dataart.common.exception;

public class TransactionLockedException extends BusinessException {
    public TransactionLockedException(String transactionId, Long productId) {
        super(transactionId, String.format("Product with id %d is locked", productId));
    }
}
