package com.dataart.payment.repository;

import com.dataart.payment.domain.Wallet;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@SuppressWarnings("unused")
public interface WalletRepository extends CrudRepository<Wallet, Long> {
    Optional<Wallet> findByCustomerId(final Long customerId);
}
