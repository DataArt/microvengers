package com.dataart.common.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorMessage {
    String transactionId;
    String message;
}