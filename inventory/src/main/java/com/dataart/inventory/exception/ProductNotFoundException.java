package com.dataart.inventory.exception;

import com.dataart.common.exception.BusinessException;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(final String transactionId, final Long productId) {
        super(transactionId, String.format("Product not found for productId: %d", productId));
    }
}
