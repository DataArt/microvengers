package com.dataart.order.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequestDTO {
    Long customerId;
    Long productId;
    Long quantity;
    BigDecimal price;
    String countryCode;
}
