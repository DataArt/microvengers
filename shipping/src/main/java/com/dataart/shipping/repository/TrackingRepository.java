package com.dataart.shipping.repository;

import com.dataart.shipping.domain.Tracking;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public interface TrackingRepository extends CrudRepository<Tracking, UUID> {
    Optional<Tracking> findByTransactionId(String transactionId);
}
