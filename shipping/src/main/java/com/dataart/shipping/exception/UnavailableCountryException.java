package com.dataart.shipping.exception;

import com.dataart.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class UnavailableCountryException extends BusinessException {
    public UnavailableCountryException(final  String transactionId, final String countryCode) {
        super(transactionId, String.format("Destination: %s not supported", countryCode));
    }
}
