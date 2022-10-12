package com.dataart.order.config;

import com.dataart.common.domain.ErrorMessage;
import com.dataart.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;

@Component
@RequiredArgsConstructor
public class ErrorHandler implements ResponseErrorHandler {

    private final OrderRepository orderRepository;

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == CLIENT_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        var errorMessage = objectMapper.readValue(response.getBody(), ErrorMessage.class);
        var transactionId = errorMessage.getTransactionId();
        var message = errorMessage.getMessage();
        if (transactionId != null) {
            orderRepository.findById(UUID.fromString(transactionId))
                    .ifPresent(order -> {
                        order.setDescription(message);
                        orderRepository.save(order);
                    });
        }
    }
}
