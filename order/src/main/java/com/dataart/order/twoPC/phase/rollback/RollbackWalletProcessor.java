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
public class RollbackWalletProcessor implements RollbackPhaseProcessor {

    private final RestTemplate restTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);

        var headers = new HttpHeaders();
        headers.set("TransactionId", transactionId);

        var url = String.format("http://localhost:8083/api/v1/accounting/wallet/rollback/%d", orderRequest.getCustomerId());
        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
    }
}
