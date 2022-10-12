package com.dataart.order.saga.command;

import com.dataart.common.dto.ShippingRequestDTO;
import com.dataart.common.dto.ShippingResponseDTO;
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
public class ShipOrderProcessor implements SagaCommandProcessor {

    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        var headers = new HttpHeaders();
        headers.set("TransactionId", transactionId);
        var request = buildShippingRequest(orderRequest);

        var response = restTemplate.exchange(
                "http://localhost:8084/api/v1/shipping",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                ShippingResponseDTO.class);

        var order = orderRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));

        order.setTrackingId(Objects.requireNonNull(response.getBody()).getTrackingId());
        orderRepository.save(order);
    }

    private ShippingRequestDTO buildShippingRequest(OrderRequestDTO request) {
        return ShippingRequestDTO.builder()
                .countryCode(request.getCountryCode())
                .build();
    }
}
