package com.reis.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reis.entities.Order;
import com.reis.entities.DTOs.OrderBalanceProjection;
import com.reis.entities.enums.Type;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findAllByType(Type type);
	List<Order> findByTypeAndDateBetween(Type type, LocalDate startDate, LocalDate finalDate);
	@Query(value = "SELECT "
            + "COALESCE(SUM(o.order_value), 0) AS totalOrders, "
            + "COALESCE(SUM(o.delivery_value), 0) AS totalDeliveries, "
            + "COALESCE(SUM(o.ifood_payment_value), 0) AS totalIfoodPayments, "
            + "COALESCE(SUM(o.ifood_comission), 0) AS totalComissions, "
            + "COALESCE(SUM(o.service_fee), 0) AS totalFees, "
            + "COALESCE(SUM(CASE WHEN o.order_type = 'VIA_IFOOD' THEN o.order_value ELSE 0 END), 0) AS totalIfood, "
            + "COALESCE(SUM(CASE WHEN o.order_type = 'VIA_PEDIDO_DIRETO' THEN o.order_value ELSE 0 END), 0) AS totalDirect, "
            + "COALESCE(SUM(CASE WHEN o.payment_method = 'DINHEIRO' THEN (CASE WHEN o.order_type = 'VIA_PEDIDO_DIRETO' THEN o.order_value ELSE COALESCE(o.ifood_direct_payment_value, 0) END) ELSE 0 END), 0) AS totalCash, "
            + "COALESCE(SUM(CASE WHEN o.payment_method = 'CARTÃO' THEN (CASE WHEN o.order_type = 'VIA_PEDIDO_DIRETO' THEN o.order_value ELSE COALESCE(o.ifood_direct_payment_value, 0) END) ELSE 0 END), 0) AS totalCard, "
            + "COALESCE(SUM(CASE WHEN o.payment_method = 'PIX' THEN (CASE WHEN o.order_type = 'VIA_PEDIDO_DIRETO' THEN o.order_value ELSE COALESCE(o.ifood_direct_payment_value, 0) END) ELSE 0 END), 0) AS totalPix "
            + "FROM tb_orders o "
            + "WHERE o.date BETWEEN :start AND :end", 
            nativeQuery = true)
    OrderBalanceProjection getBalanceByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
