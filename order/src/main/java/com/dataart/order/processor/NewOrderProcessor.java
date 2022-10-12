package com.dataart.order.processor;

import com.dataart.order.common.OrderStatus;
import com.dataart.order.domain.Order;
import com.dataart.order.dto.OrderRequestDTO;
import com.dataart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class NewOrderProcessor implements Processor {

    private final OrderRepository orderRepository;
    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        var requestDTO = exchange.getIn().getBody(OrderRequestDTO.class);
        var order = Order.builder()
                .customerId(requestDTO.getCustomerId())
                .status(OrderStatus.INITIATED)
                .build();
        orderRepository.save(order);
        exchange.getIn().setHeader("transactionId", order.getId());
    }
}
