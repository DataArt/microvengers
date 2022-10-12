package com.dataart.customer.web;

import com.dataart.common.domain.ErrorMessage;
import com.dataart.common.dto.CustomerResponseDTO;
import com.dataart.common.exception.BusinessException;
import com.dataart.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @GetMapping("api/v1/customer/{id}")
    public CustomerResponseDTO getCustomerById(@RequestHeader final String transactionId, @PathVariable final Long id) {
        return service.getCustomerById(transactionId, id);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorMessage> handleCustomerNotFoundException(final BusinessException exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorMessage.builder()
                        .transactionId(exception.getTransactionId())
                        .message(exception.getMessage())
                        .build());
    }
}