package com.dataart.inventory.service;

import com.dataart.inventory.domain.Invoice;
import com.dataart.inventory.exception.InsufficientQuantityException;
import com.dataart.inventory.exception.ProductNotFoundException;
import com.dataart.common.dto.InventoryRequestDTO;
import com.dataart.common.dto.InventoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventorySagaService {

    private final InventoryDatabaseService databaseService;

    @Transactional
    public InventoryResponseDTO requestInvoice(final String transactionId, final InventoryRequestDTO request) {
        var invoice = databaseService.getInvoice(transactionId)
                .orElse(newInvoice(transactionId, request));

        return InventoryResponseDTO.builder()
                .invoiceId(invoice.getId().toString())
                .price(invoice.getProduct().getPrice())
                .build();
    }

    private Invoice newInvoice(final String transactionId, final InventoryRequestDTO request) {
        var productId = request.getProductId();
        var product = databaseService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(transactionId, productId));
        if (product.getQuantity() < request.getQuantity()) {
            throw new InsufficientQuantityException(transactionId, productId);
        }

        var invoice = Invoice.builder()
                .transactionId(transactionId)
                .count(request.getQuantity())
                .product(product)
                .active(true)
                .build();
        databaseService.persistInvoice(invoice);

        product.setQuantity(product.getQuantity() - request.getQuantity());
        product.getInvoices().add(invoice);
        return invoice;
    }

    @Transactional
    public void cancelInvoice(final String invoiceId) {
        databaseService.getInvoice(invoiceId)
            .ifPresent(invoice -> {
                var product = invoice.getProduct();
                product.setQuantity(product.getQuantity() + invoice.getCount());
                invoice.setActive(false);
            });
    }

}
