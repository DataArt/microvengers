package com.dataart.inventory.repository;

import com.dataart.inventory.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unused")
public interface ProductRepository extends JpaRepository<Product, Long> {

}
