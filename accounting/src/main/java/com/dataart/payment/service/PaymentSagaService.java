package com.dataart.payment.service;

import com.dataart.payment.domain.Payment;
import com.dataart.payment.exception.InsufficientFundsException;
import com.dataart.common.dto.PaymentRequestDTO;
import com.dataart.common.dto.PaymentResponseDTO;
import com.dataart.payment.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentSagaService {

    private final PaymentDatabaseService databaseService;

    @Transactional
    public PaymentResponseDTO makePayment(final String transactionId, final PaymentRequestDTO request) {
        var payment = databaseService.getPayment(transactionId)
                .orElse(newPayment(transactionId, request));

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId().toString())
                .build();
    }

    private Payment newPayment(final String transactionId, final PaymentRequestDTO request) {
        var customerId = request.getCustomerId();
        var wallet = databaseService.getWalletByCustomerId(customerId)
                .orElseThrow(() -> new WalletNotFoundException(transactionId, customerId));
        if (wallet.getAmount().compareTo(request.getPrice()) < 0) {
            throw new InsufficientFundsException(transactionId, wallet.getId());
        }

        var payment = Payment.builder()
                .transactionId(transactionId)
                .amount(request.getPrice())
                .wallet(wallet)
                .initiatedAt(LocalDateTime.now())
                .active(true)
                .build();
        databaseService.persistPayment(payment);

        wallet.setAmount(wallet.getAmount().subtract(request.getPrice()));
        wallet.getPayments().add(payment);
        return payment;
    }

    @Transactional
    public void refundPayment(final String paymentId) {
        databaseService.getPayment(paymentId)
                .ifPresent(payment -> {
                    var wallet = payment.getWallet();
                    wallet.setAmount(wallet.getAmount().add(payment.getAmount()));
                    payment.setActive(false);
                });
    }
}
