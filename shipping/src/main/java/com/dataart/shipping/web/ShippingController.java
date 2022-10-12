package com.dataart.shipping.web;

import com.dataart.common.domain.ErrorMessage;
import com.dataart.common.dto.ShippingRequestDTO;
import com.dataart.common.dto.ShippingResponseDTO;
import com.dataart.common.exception.BusinessException;
import com.dataart.shipping.service.Shipping2PCService;
import com.dataart.shipping.service.ShippingSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ShippingController {
    public static final String SHIPPING_API = "api/v1/shipping";
    private final ShippingSagaService sagaService;
    private final Shipping2PCService twoPhaseCommitService;

    /**
     * SAGA endpoints
     */

    @PutMapping(SHIPPING_API)
    public ShippingResponseDTO shipOrder(@RequestHeader final String transactionId, @RequestBody final ShippingRequestDTO request) {
        return sagaService.shipOrder(transactionId, request);
    }

    @DeleteMapping(SHIPPING_API + "/{transactionId}")
    public void cancelShipping(@PathVariable final String transactionId) {
        sagaService.cancelShipping(transactionId);
    }

    /**
     * 2PC endpoints
     */

    @PutMapping(SHIPPING_API + "/prepare")
    public ResponseEntity<Void> preparePhase(@RequestHeader final String transactionId, @RequestBody final ShippingRequestDTO request) {
        twoPhaseCommitService.preparePhaseProduct(transactionId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(SHIPPING_API + "/commit")
    public ShippingResponseDTO commitPhase(@RequestHeader final String transactionId) {
        return twoPhaseCommitService.commitPhaseInvoice(transactionId);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorMessage> handleException(final BusinessException exception) {
        log.error("Exception message: {}", exception.getMessage());
        return ResponseEntity.status(409)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorMessage.builder()
                        .transactionId(exception.getTransactionId())
                        .message(exception.getMessage())
                        .build());
    }
}
