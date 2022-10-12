package com.dataart.inventory.service;

import com.dataart.inventory.domain.Invoice;
import com.dataart.inventory.domain.Product;
import com.dataart.inventory.repository.InvoiceRepository;
import com.dataart.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryDatabaseService {

    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Invoice> getInvoice(final String invoiceId) {
        return invoiceRepository.findById(UUID.fromString(invoiceId));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Product> getProductById(final Long productId) {
        return productRepository.findById(productId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void persistInvoice(final Invoice invoice) {
        invoiceRepository.save(invoice);
    }

}
