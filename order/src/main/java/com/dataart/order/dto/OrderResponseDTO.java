package com.dataart.order.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponseDTO {
    String transactionId;
    Long customerId;
    String paymentId;
    String inventoryId;
    String trackingId;
    String status;
    String description;
}
