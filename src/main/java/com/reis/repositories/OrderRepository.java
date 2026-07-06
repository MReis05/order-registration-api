package com.reis.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.entities.Order;
import com.reis.entities.enums.Type;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findAllByType(Type type);
}
