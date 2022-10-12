package com.dataart.order.twoPC.phase.prepare;

import com.dataart.common.dto.InventoryRequestDTO;
import com.dataart.common.dto.InventoryResponseDTO;
import com.dataart.order.dto.OrderRequestDTO;
import com.dataart.order.twoPC.exception.TransactionPrepareException;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PrepareProductProcessor implements PreparePhaseProcessor {

    private final RestTemplate restTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        try {
            var headers = new HttpHeaders();
            headers.set("TransactionId", transactionId);
            var request = buildInventoryRequest(orderRequest);

            var response = restTemplate.exchange(
                    "http://localhost:8082/api/v1/inventory/product/prepare",
                    HttpMethod.PUT,
                    new HttpEntity<>(request, headers),
                    InventoryResponseDTO.class);
            orderRequest.setPrice(Objects.requireNonNull(response.getBody()).getPrice());
        } catch (Exception ex) {
            throw new TransactionPrepareException(ex.getMessage());
        }
    }

    private InventoryRequestDTO buildInventoryRequest(OrderRequestDTO request) {
        return InventoryRequestDTO.builder()
                .quantity(request.getQuantity())
                .productId(request.getProductId())
                .build();
    }

}
