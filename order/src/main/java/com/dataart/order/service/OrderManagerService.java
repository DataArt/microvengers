package com.dataart.order.service;

import com.dataart.order.dto.OrderResponseDTO;
import com.dataart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderManagerService {
    private final OrderRepository orderRepository;

    public OrderResponseDTO getOrder(String transactionID) {
        var order = orderRepository.findById(UUID.fromString(transactionID))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));

        return OrderResponseDTO.builder()
                .transactionId(order.getId().toString())
                .customerId(order.getCustomerId())
                .paymentId(order.getPaymentId())
                .inventoryId(order.getInvoiceId())
                .trackingId(order.getTrackingId())
                .status(order.getStatus().toString())
                .description(order.getDescription())
                .build();
    }
}
