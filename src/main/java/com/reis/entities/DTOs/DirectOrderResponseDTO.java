package com.reis.entities.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reis.entities.DirectOrder;
import com.reis.entities.enums.PaymentMethod;

public record DirectOrderResponseDTO(
		Long id,
		LocalDate date,
		BigDecimal orderValue,
		BigDecimal deliveryValue,
		PaymentMethod paymentMethod
		) {
	
	public DirectOrderResponseDTO(DirectOrder entity) {
		this(
			entity.getId(),
			entity.getDate(),
			entity.getOrderValue(),
			entity.getDeliveryValue(),
			entity.getPaymentMethod()
			);
	}
}
