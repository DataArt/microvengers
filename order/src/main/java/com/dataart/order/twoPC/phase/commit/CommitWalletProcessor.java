package com.dataart.order.twoPC.phase.commit;

import com.dataart.common.dto.PaymentRequestDTO;
import com.dataart.common.dto.PaymentResponseDTO;
import com.dataart.order.dto.OrderRequestDTO;
import com.dataart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommitWalletProcessor implements CommitPhaseProcessor {

    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        var headers = new HttpHeaders();
        headers.set("TransactionId", transactionId);
        var request = buildPaymentRequest(orderRequest);

        var response = restTemplate.exchange(
                "http://localhost:8083/api/v1/accounting/wallet/commit",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                PaymentResponseDTO.class);

        var order = orderRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));

        order.setPaymentId(Objects.requireNonNull(response.getBody()).getPaymentId());
        orderRepository.save(order);
    }

    private PaymentRequestDTO buildPaymentRequest(OrderRequestDTO request) {
        return PaymentRequestDTO.builder()
                .customerId(request.getCustomerId())
                .price(request.getPrice())
                .build();
    }
}
