package com.dataart.order.processor;


import com.dataart.order.dto.OrderRequestDTO;
import com.dataart.common.dto.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CheckCustomerProcessor implements Processor {

    private final RestTemplate restTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        var headers = new HttpHeaders();
        headers.set("TransactionId", transactionId);
        var entity = new HttpEntity<String>(headers);

        var response = restTemplate.exchange(
                String.format("http://localhost:8081/api/v1/customer/%d", orderRequest.getCustomerId()),
                HttpMethod.GET,
                entity,
                CustomerResponseDTO.class);

        if (Objects.requireNonNull(response.getBody()).isBlocked()) {
            throw new RuntimeException("Can't reserve. Customer is blocked!!!");
        }
    }
}
