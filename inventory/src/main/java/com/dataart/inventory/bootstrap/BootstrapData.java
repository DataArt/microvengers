package com.dataart.inventory.bootstrap;

import com.dataart.inventory.domain.Product;
import com.dataart.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        productRepository.save(Product.builder().name("Hammer").description("for Thor").quantity(1L).price(BigDecimal.valueOf(100)).locked(false).build());
        productRepository.save(Product.builder().name("Shield").description("for Captain").quantity(2L).price(BigDecimal.valueOf(300)).locked(false).build());
        productRepository.save(Product.builder().name("Iron Suit").description("for IronMan").quantity(2L).price(BigDecimal.valueOf(500)).locked(false).build());
        productRepository.save(Product.builder().name("The Infinity Stone").description("for Thanos").price(BigDecimal.valueOf(1000)).quantity(1L).locked(false).build());
    }
}
