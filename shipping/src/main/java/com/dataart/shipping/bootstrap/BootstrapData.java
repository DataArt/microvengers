package com.dataart.shipping.bootstrap;

import com.dataart.shipping.domain.Country;
import com.dataart.shipping.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final CountryRepository countryRepository;

    @Override
    public void run(String... args) throws Exception {
        countryRepository.save(Country.builder().code("KZ").active(true).build());
        countryRepository.save(Country.builder().code("USA").active(true).build());
        countryRepository.save(Country.builder().code("UA").active(true).build());
        countryRepository.save(Country.builder().code("RU").active(false).build());
    }
}
