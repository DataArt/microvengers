package com.dataart.inventory.web;

import com.dataart.common.domain.ErrorMessage;
import com.dataart.common.dto.InventoryRequestDTO;
import com.dataart.common.dto.InventoryResponseDTO;
import com.dataart.common.exception.BusinessException;
import com.dataart.inventory.service.Inventory2PCService;
import com.dataart.inventory.service.InventorySagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    public static final String INVENTORY_API = "api/v1/inventory";

    private final InventorySagaService sagaService;
    private final Inventory2PCService twoPhaseCommitService;

    /**
     * SAGA endpoints
     */

    @PutMapping(INVENTORY_API + "/invoice")
    public InventoryResponseDTO requestInvoice(@RequestHeader final String transactionId, @RequestBody final InventoryRequestDTO request) {
        return sagaService.requestInvoice(transactionId, request);
    }

    @DeleteMapping(INVENTORY_API + "/invoice/{invoiceId}")
    public void cancelInvoice(@PathVariable final String invoiceId) {
        sagaService.cancelInvoice(invoiceId);
    }

    /**
     * 2PC endpoints
     */

    @PutMapping(INVENTORY_API + "/product/prepare")
    public InventoryResponseDTO preparePhase(@RequestHeader final String transactionId, @RequestBody final InventoryRequestDTO request) {
        return twoPhaseCommitService.preparePhaseProduct(transactionId, request);
    }

    @PutMapping(INVENTORY_API + "/product/commit")
    public InventoryResponseDTO commitPhase(@RequestHeader final String transactionId, @RequestBody final InventoryRequestDTO request) {
        return twoPhaseCommitService.commitPhaseProduct(transactionId, request);
    }

    @DeleteMapping(INVENTORY_API + "/product/rollback/{productId}")
    public void rollbackPhase(@RequestHeader final String transactionId, @PathVariable final Long productId) {
        twoPhaseCommitService.rollbackProduct(transactionId, productId);
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
