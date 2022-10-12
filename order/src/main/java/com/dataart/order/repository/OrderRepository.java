package com.dataart.order.repository;

import com.dataart.order.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@SuppressWarnings("unused")
public interface OrderRepository extends CrudRepository<Order, UUID> {

}
