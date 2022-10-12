package com.dataart.order.twoPC.phase.commit;

import com.dataart.common.dto.InventoryRequestDTO;
import com.dataart.common.dto.InventoryResponseDTO;
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
public class CommitProductProcessor implements CommitPhaseProcessor {

    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        var headers = new HttpHeaders();
        headers.set("TransactionId", transactionId);
        var request = buildInventoryRequest(orderRequest);

        var response = restTemplate.exchange(
                "http://localhost:8082/api/v1/inventory/product/commit",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                InventoryResponseDTO.class);

        var order = orderRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));

        order.setInvoiceId(Objects.requireNonNull(response.getBody()).getInvoiceId());
        orderRepository.save(order);
    }

    private InventoryRequestDTO buildInventoryRequest(OrderRequestDTO request) {
        return InventoryRequestDTO.builder()
                .quantity(request.getQuantity())
                .productId(request.getProductId())
                .build();
    }
}
