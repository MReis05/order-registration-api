package com.reis.entities.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reis.entities.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record IfoodOrderRequestDTO(
		@NotNull(message = "O valor do pedido não pode ser nulo.")
		@PositiveOrZero(message = "O valor do pedido não pode ser negativo.")
		BigDecimal orderValue,
		@NotNull(message = "O valor da entrega não pode ser nulo.")
		@PositiveOrZero(message = "O valor da entrega não pode ser negativo.")
		BigDecimal deliveryValue,
		@NotNull(message = "O metódo de pagamento é obrigatório.")
		PaymentMethod method,
		BigDecimal paymentValue,
		boolean isSplitPayment,
		boolean doesHaveServiceFee,
		LocalDate date
		) {
}
