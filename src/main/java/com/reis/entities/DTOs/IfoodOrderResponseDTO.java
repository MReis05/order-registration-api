package com.reis.entities.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.reis.entities.IfoodOrder;
import com.reis.entities.enums.Category;
import com.reis.entities.enums.PaymentMethod;

public record IfoodOrderResponseDTO(
    Long id,
    LocalDate date,
    BigDecimal orderValue,
    BigDecimal deliveryValue,
    PaymentMethod paymentMethod,
    Category category,
    BigDecimal ifoodComission
) {
   
    public IfoodOrderResponseDTO(IfoodOrder entity) {
        this(
            entity.getId(),
            entity.getDate(),
            entity.getOrderValue(),
            entity.getDeliveryValue(),
            entity.getPaymentMethod(),
            entity.getCategory(),
            entity.getIfoodComission()
        );
    }
}