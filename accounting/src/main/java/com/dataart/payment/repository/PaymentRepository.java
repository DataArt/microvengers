package com.dataart.payment.repository;

import com.dataart.payment.domain.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@SuppressWarnings("unused")
public interface PaymentRepository extends CrudRepository<Payment, UUID> {
}
