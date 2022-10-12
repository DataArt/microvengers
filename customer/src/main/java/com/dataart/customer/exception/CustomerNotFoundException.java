package com.dataart.customer.exception;

import com.dataart.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class CustomerNotFoundException extends BusinessException {
    public CustomerNotFoundException(final String transactionId, final Long customerId) {
        super(transactionId, String.format("Customer not found for customerId: %d", customerId));
    }
}
