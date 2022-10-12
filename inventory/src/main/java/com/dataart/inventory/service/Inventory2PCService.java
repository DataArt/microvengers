package com.dataart.inventory.service;

import com.dataart.common.dto.InventoryRequestDTO;
import com.dataart.common.dto.InventoryResponseDTO;
import com.dataart.common.exception.TransactionLockedException;
import com.dataart.inventory.domain.Invoice;
import com.dataart.inventory.exception.InsufficientQuantityException;
import com.dataart.inventory.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class Inventory2PCService {

    private final InventoryDatabaseService databaseService;

    @Transactional
    public InventoryResponseDTO preparePhaseProduct(final String transactionId, final InventoryRequestDTO request) {
        log.info("preparePhaseProduct with transactionId {} and productId {}", transactionId, request.getProductId());
        var productId = request.getProductId();
        var product = databaseService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(transactionId, productId));
        if (product.getQuantity() < request.getQuantity())
            throw new InsufficientQuantityException(transactionId, productId);
        if (product.isLocked())
            throw new TransactionLockedException(transactionId, productId);
        product.setLocked(true);
        return InventoryResponseDTO.builder()
                .price(product.getPrice())
                .build();
    }

    @Transactional
    public void rollbackProduct(final String transactionId, final Long productId) {
        var product = databaseService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(transactionId, productId));
        product.setLocked(false);
    }

    @Transactional
    public InventoryResponseDTO commitPhaseProduct(final String transactionId, final InventoryRequestDTO request) {
        log.info("commitPhaseInvoice with transactionId {} and productId {}", transactionId, request.getProductId());
        var invoice = databaseService.getInvoice(transactionId)
                .orElse(commitInvoice(transactionId, request));

        return InventoryResponseDTO.builder()
                .invoiceId(invoice.getId().toString())
                .build();
    }

    private Invoice commitInvoice(final String transactionId, final InventoryRequestDTO request) {
        var productId = request.getProductId();
        var product = databaseService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(transactionId, productId));

        var invoice = Invoice.builder()
                .transactionId(transactionId)
                .count(request.getQuantity())
                .product(product)
                .active(true)
                .build();
        databaseService.persistInvoice(invoice);

        product.setQuantity(product.getQuantity() - request.getQuantity());
        product.getInvoices().add(invoice);
        product.setLocked(false);
        return invoice;
    }
}
