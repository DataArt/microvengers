package com.dataart.shipping.service;

import com.dataart.common.dto.ShippingRequestDTO;
import com.dataart.common.dto.ShippingResponseDTO;
import com.dataart.shipping.domain.Tracking;
import com.dataart.shipping.exception.UnavailableCountryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@Slf4j
@RequiredArgsConstructor
public class Shipping2PCService {

    private final ShippingDatabaseService databaseService;

    @Transactional
    public void preparePhaseProduct(@RequestHeader final String transactionId,  final ShippingRequestDTO request) {
        databaseService.getActiveCountryByCode(request.getCountryCode())
                .orElseThrow(() -> new UnavailableCountryException(transactionId, request.getCountryCode()));
    }

    @Transactional
    public ShippingResponseDTO commitPhaseInvoice(final String transactionId) {
        var tracking = databaseService.getTrackingByTransactionId(transactionId)
                .orElse(commitShipping(transactionId));

        return ShippingResponseDTO.builder()
                .trackingId(tracking.getId().toString())
                .build();
    }


    private Tracking commitShipping(final String transactionId) {
        var tracking = Tracking.builder()
                .transactionId(transactionId)
                .active(true)
                .build();

        return databaseService.saveTracking(tracking);
    }
}
