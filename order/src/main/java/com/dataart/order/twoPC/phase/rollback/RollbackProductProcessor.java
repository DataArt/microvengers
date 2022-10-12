package com.dataart.order.twoPC.phase.rollback;

import com.dataart.order.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RollbackProductProcessor implements RollbackPhaseProcessor {

    private final RestTemplate restTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);

        var headers = new HttpHeaders();
        headers.set("TransactionId", transactionId);

        var url = String.format("http://localhost:8082/api/v1/inventory/product/rollback/%d", orderRequest.getProductId());
        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);
    }
}
