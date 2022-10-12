package com.dataart.customer.bootstrap;

import com.dataart.customer.domain.Customer;
import com.dataart.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        customerRepository.save(Customer.builder().name("Tony Stark").blocked(false).build());
        customerRepository.save(Customer.builder().name("Steve Rodgers").blocked(false).build());
        customerRepository.save(Customer.builder().name("Natasha Romanoff").blocked(false).build());
        customerRepository.save(Customer.builder().name("Bruce Banner").blocked(false).build());
        customerRepository.save(Customer.builder().name("Thanos").blocked(true).build());
    }
}
