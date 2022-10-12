package com.dataart.payment.exception;

import com.dataart.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException(final String transactionId, final Long walletId) {
        super(transactionId, String.format("InsufficientFunds: walletId %d", walletId));
    }
}
