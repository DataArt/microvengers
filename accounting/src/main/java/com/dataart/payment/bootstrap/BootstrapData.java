package com.dataart.payment.bootstrap;

import com.dataart.payment.domain.Wallet;
import com.dataart.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) throws Exception {
        walletRepository.save(Wallet.builder().amount(BigDecimal.valueOf(100)).customerId(1L).locked(false).build());
        walletRepository.save(Wallet.builder().amount(BigDecimal.valueOf(1000)).customerId(2L).locked(false).build());
        walletRepository.save(Wallet.builder().amount(BigDecimal.valueOf(1000)).customerId(3L).locked(false).build());
        walletRepository.save(Wallet.builder().amount(BigDecimal.valueOf(1000)).customerId(4L).locked(false).build());
    }
}
