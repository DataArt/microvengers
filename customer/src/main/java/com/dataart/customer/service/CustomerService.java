package com.dataart.customer.service;

import com.dataart.common.dto.CustomerResponseDTO;
import com.dataart.customer.exception.CustomerNotFoundException;
import com.dataart.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    public CustomerResponseDTO getCustomerById(final String transactionId, final Long id) {
        return repository.findById(id)
                .map(customer ->
                        CustomerResponseDTO.builder()
                                .id(customer.getId())
                                .name(customer.getName())
                                .blocked(customer.isBlocked())
                                .build()
                )
                .orElseThrow(() -> new CustomerNotFoundException(transactionId, id));
    }
}
