package com.dataart.payment.web;

import com.dataart.common.domain.ErrorMessage;
import com.dataart.common.dto.PaymentRequestDTO;
import com.dataart.common.dto.PaymentResponseDTO;
import com.dataart.common.exception.BusinessException;
import com.dataart.payment.service.Payment2PCService;
import com.dataart.payment.service.PaymentSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountingController {
    public static final String PAYMENT_API = "api/v1/accounting";
    private final PaymentSagaService sagaService;
    private final Payment2PCService twoPhaseCommitService;

    /**
     * 2PC endpoints
     */
    @PutMapping(PAYMENT_API + "/payment")
    public PaymentResponseDTO makePayment(@RequestHeader final String transactionId, @RequestBody final PaymentRequestDTO request) {
        return sagaService.makePayment(transactionId, request);
    }

    @DeleteMapping(PAYMENT_API + "/payment/{paymentId}")
    public void refundPayment(@PathVariable final String paymentId) {
        sagaService.refundPayment(paymentId);
    }

    /**
     * 2PC endpoints
     */

    @PutMapping(PAYMENT_API + "/wallet/prepare")
    public ResponseEntity<Void> preparePhase(@RequestHeader final String transactionId, @RequestBody final PaymentRequestDTO request) {
        twoPhaseCommitService.preparePhaseProduct(transactionId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(PAYMENT_API + "/wallet/commit")
    public PaymentResponseDTO commitPhase(@RequestHeader final String transactionId, @RequestBody final PaymentRequestDTO request) {
        return twoPhaseCommitService.commitPhaseInvoice(transactionId, request);
    }

    @DeleteMapping(PAYMENT_API + "/wallet/rollback/{customerId}")
    public void rollbackPhase(@RequestHeader final String transactionId, @PathVariable final Long customerId) {
        twoPhaseCommitService.rollbackWallet(transactionId, customerId);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorMessage> handleCustomerNotFoundException(final BusinessException exception) {
        log.error("Exception message: {}", exception.getMessage());
        return ResponseEntity.status(409)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorMessage.builder()
                        .transactionId(exception.getTransactionId())
                        .message(exception.getMessage())
                        .build());
    }
}
