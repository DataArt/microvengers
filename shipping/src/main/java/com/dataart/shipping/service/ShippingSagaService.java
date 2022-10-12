package com.dataart.shipping.service;

import com.dataart.shipping.domain.Tracking;
import com.dataart.shipping.exception.UnavailableCountryException;
import com.dataart.common.dto.ShippingRequestDTO;
import com.dataart.common.dto.ShippingResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingSagaService {

    private final ShippingDatabaseService databaseService;

    @Transactional
    public ShippingResponseDTO shipOrder(final String transactionId, final ShippingRequestDTO request) {
        var tracking = databaseService.getTrackingByTransactionId(transactionId)
                .orElse(newShipping(transactionId, request));

        return ShippingResponseDTO.builder()
                .trackingId(tracking.getId().toString())
                .build();
    }

    private Tracking newShipping(final String transactionId, final ShippingRequestDTO request) {
        if (databaseService.getActiveCountryByCode(request.getCountryCode()).isEmpty())
            throw new UnavailableCountryException(transactionId, request.getCountryCode());

        var tracking = Tracking.builder()
                .transactionId(transactionId)
                .active(true)
                .build();

        return databaseService.saveTracking(tracking);
    }

    @Transactional
    public void cancelShipping(final String transactionId) {
        databaseService.getTrackingByTransactionId(transactionId)
                .ifPresent(tracking -> tracking.setActive(false));
    }
}
