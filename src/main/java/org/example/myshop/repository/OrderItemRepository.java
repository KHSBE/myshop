package org.example.myshop.repository;

import org.example.myshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
    Optional<Object> findByid(Long id);
}
