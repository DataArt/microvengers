package com.dataart.order.saga.compensation;

import com.dataart.order.common.OrderStatus;
import com.dataart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefundPaymentProcessor implements SagaCompensationProcessor {

    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);
        var order = orderRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));
        var paymentId = order.getPaymentId();

        restTemplate.delete(String.format("http://localhost:8083/api/v1/accounting/payment/%s", paymentId));


        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
    }
}
