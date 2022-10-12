package com.dataart.payment.service;

import com.dataart.common.dto.PaymentRequestDTO;
import com.dataart.common.dto.PaymentResponseDTO;
import com.dataart.common.exception.TransactionLockedException;
import com.dataart.payment.domain.Payment;
import com.dataart.payment.exception.InsufficientFundsException;
import com.dataart.payment.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class Payment2PCService {

    private final PaymentDatabaseService databaseService;

    @Transactional
    public void preparePhaseProduct(final String transactionId, final PaymentRequestDTO request) {
        var customerId = request.getCustomerId();
        var wallet = databaseService.getWalletByCustomerId(customerId)
                .orElseThrow(() -> new WalletNotFoundException(transactionId, customerId));
        if (wallet.getAmount().compareTo(request.getPrice()) < 0)
            throw new InsufficientFundsException(transactionId, wallet.getId());
        if (wallet.isLocked())
            throw new TransactionLockedException(transactionId, customerId);
        wallet.setLocked(true);
    }

    @Transactional
    public PaymentResponseDTO commitPhaseInvoice(final String transactionId, final PaymentRequestDTO request) {
        var payment = databaseService.getPayment(transactionId)
                .orElse(commitPayment(transactionId, request));

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId().toString())
                .build();
    }

    @Transactional
    public void rollbackWallet(final String transactionId, final Long customerId) {
        var wallet = databaseService.getWalletByCustomerId(customerId)
                .orElseThrow(() -> new WalletNotFoundException(transactionId, customerId));
        wallet.setLocked(false);
    }

    private Payment commitPayment(final String transactionId, final PaymentRequestDTO request) {
        var customerId = request.getCustomerId();
        var wallet = databaseService.getWalletByCustomerId(customerId)
                .orElseThrow(() -> new WalletNotFoundException(transactionId, customerId));

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
        wallet.setLocked(false);
        return payment;
    }
}
