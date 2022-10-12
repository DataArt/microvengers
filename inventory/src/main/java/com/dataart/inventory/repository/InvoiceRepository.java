package com.dataart.inventory.repository;

import com.dataart.inventory.domain.Invoice;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@SuppressWarnings("unused")
public interface InvoiceRepository extends CrudRepository<Invoice, UUID> {
}
