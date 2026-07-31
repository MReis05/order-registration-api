package com.reis.entities.DTOs;

import java.math.BigDecimal;

public interface OrderBalanceProjection {

	BigDecimal getTotalOrders();
    BigDecimal getTotalDeliveries();
    BigDecimal getTotalIfoodPayments();
    BigDecimal getTotalComissions();
    BigDecimal getTotalFees();
    BigDecimal getTotalIfood();
    BigDecimal getTotalDirect();
    BigDecimal getTotalCash();
    BigDecimal getTotalCard();
    BigDecimal getTotalPix();
}
