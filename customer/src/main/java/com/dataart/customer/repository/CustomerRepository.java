package com.dataart.customer.repository;

import com.dataart.customer.domain.Customer;
import org.springframework.data.repository.CrudRepository;

@SuppressWarnings("unused")
public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
