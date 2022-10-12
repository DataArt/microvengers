package com.dataart.shipping.service;

import com.dataart.shipping.domain.Country;
import com.dataart.shipping.domain.Tracking;
import com.dataart.shipping.repository.CountryRepository;
import com.dataart.shipping.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingDatabaseService {

    private final TrackingRepository trackingRepository;
    private final CountryRepository countryRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Tracking> getTrackingByTransactionId(final String transactionId) {
        return trackingRepository.findByTransactionId(transactionId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Country> getActiveCountryByCode(final String code) {
        return countryRepository.findByCodeAndActive(code, Boolean.TRUE);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Tracking saveTracking(final Tracking tracking) {
        return trackingRepository.save(tracking);
    }

}
