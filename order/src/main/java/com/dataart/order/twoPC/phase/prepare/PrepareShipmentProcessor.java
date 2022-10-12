package com.dataart.order.twoPC.phase.prepare;

import com.dataart.common.dto.ShippingRequestDTO;
import com.dataart.common.dto.ShippingResponseDTO;
import com.dataart.order.dto.OrderRequestDTO;
import com.dataart.order.twoPC.exception.TransactionPrepareException;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PrepareShipmentProcessor implements PreparePhaseProcessor {

    private final RestTemplate restTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        try {
            var headers = new HttpHeaders();
            headers.set("TransactionId", transactionId);
            var request = buildShippingRequest(orderRequest);

            restTemplate.exchange(
                    "http://localhost:8084/api/v1/shipping/prepare",
                    HttpMethod.PUT,
                    new HttpEntity<>(request, headers),
                    ShippingResponseDTO.class
            );
        } catch (Exception ex) {
            throw new TransactionPrepareException(ex.getMessage());
        }
    }

    private ShippingRequestDTO buildShippingRequest(OrderRequestDTO request) {
        return ShippingRequestDTO.builder()
                .countryCode(request.getCountryCode())
                .build();
    }
}
