package com.dataart.payment.exception;

import com.dataart.common.exception.BusinessException;

public class WalletNotFoundException extends BusinessException {

    public WalletNotFoundException(final String transactionId, final Long customerId) {
        super(transactionId, String.format("Wallet not found for customerId %d", customerId));
    }
}
