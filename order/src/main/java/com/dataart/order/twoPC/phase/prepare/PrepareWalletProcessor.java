package com.dataart.order.twoPC.phase.prepare;

import com.dataart.common.dto.PaymentRequestDTO;
import com.dataart.common.dto.PaymentResponseDTO;
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
public class PrepareWalletProcessor implements PreparePhaseProcessor {

    private final RestTemplate restTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        var orderRequest = exchange.getMessage().getBody(OrderRequestDTO.class);
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        try {
            var headers = new HttpHeaders();
            headers.set("TransactionId", transactionId);
            var request = buildPaymentRequest(orderRequest);

            restTemplate.exchange(
                    "http://localhost:8083/api/v1/accounting/wallet/prepare",
                    HttpMethod.PUT,
                    new HttpEntity<>(request, headers),
                    PaymentResponseDTO.class
            );
        } catch (Exception ex) {
            throw new TransactionPrepareException(ex.getMessage());
        }
    }

    private PaymentRequestDTO buildPaymentRequest(OrderRequestDTO request) {
        return PaymentRequestDTO.builder()
                .customerId(request.getCustomerId())
                .price(request.getPrice())
                .build();
    }
}
