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
public class CancelInvoiceProcessor implements SagaCompensationProcessor {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);
        var order = orderRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));
        var invoiceId = order.getInvoiceId();

        restTemplate.delete(String.format("http://localhost:8082/api/v1/inventory/invoice/%s", invoiceId));

        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
    }
}
