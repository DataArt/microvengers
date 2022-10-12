package com.dataart.order.web;

import com.dataart.order.dto.OrderRequestDTO;
import com.dataart.order.dto.OrderResponseDTO;
import com.dataart.order.service.OrderManagerService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    public static final String ORDER_API = "api/v1/orders";
    private final ProducerTemplate producerTemplate;
    private final OrderManagerService orderManagerService;

    @GetMapping(ORDER_API + "/{transactionId}")
    public OrderResponseDTO getOrder(@PathVariable final String transactionId) {
        return orderManagerService.getOrder(transactionId);
    }

    @PostMapping(ORDER_API + "/saga")
    public ResponseEntity<Void> createOrder(@RequestBody final OrderRequestDTO request) {
        var responseExchange = producerTemplate
                .send("direct:orderSaga", exchange -> exchange.getIn().setBody(request, OrderRequestDTO.class));
        var transactionId = responseExchange.getIn().getHeader("transactionId", String.class);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, String.join("/", ORDER_API, transactionId))
                .build();
    }

    @PostMapping(ORDER_API + "/2pc")
    public ResponseEntity<Void> createOrder2PC(@RequestBody final OrderRequestDTO request) {
        var responseExchange = producerTemplate
                .send("direct:order2PC", exchange -> exchange.getIn().setBody(request, OrderRequestDTO.class));
        var transactionId = responseExchange.getIn().getHeader("transactionId", String.class);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, String.join("/", ORDER_API, transactionId))
                .build();
    }

}
