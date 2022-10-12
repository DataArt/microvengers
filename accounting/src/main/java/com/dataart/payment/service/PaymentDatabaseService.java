package com.dataart.payment.service;

import com.dataart.payment.domain.Payment;
import com.dataart.payment.domain.Wallet;
import com.dataart.payment.repository.PaymentRepository;
import com.dataart.payment.repository.WalletRepository;
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
public class PaymentDatabaseService {

    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Payment> getPayment(final String paymentId) {
        return paymentRepository.findById(UUID.fromString(paymentId));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Wallet> getWalletByCustomerId(final Long customerId) {
        return walletRepository.findByCustomerId(customerId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void persistPayment(Payment payment) {
        paymentRepository.save(payment);
    }

}
