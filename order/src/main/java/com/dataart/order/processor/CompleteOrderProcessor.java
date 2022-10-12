package com.dataart.order.processor;

import com.dataart.order.common.OrderStatus;
import com.dataart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompleteOrderProcessor implements Processor {

    private final OrderRepository orderRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        var transactionId = exchange.getIn().getHeader("transactionId", String.class);

        var order = orderRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not Found!"));

        order.setStatus(OrderStatus.COMPLETED);
        order.setDescription("Order handling completed successfully");
        orderRepository.save(order);
    }
}
