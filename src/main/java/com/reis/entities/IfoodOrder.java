package com.reis.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.reis.entities.enums.Category;

import jakarta.persistence.Entity;

@Entity
public class IfoodOrder  extends Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BigDecimal ifoodDirectPaymentValue;
	private BigDecimal ifoodPaymentValue;
	private BigDecimal ifoodComission;
	private BigDecimal serviceFee;
	private Category category;
	
	public IfoodOrder() {
	}
	
	public IfoodOrder(Order order) {
		super(order.getOrderValue(), order.getDeliveryValue(), order.getPaymentMethod(), order.getType(), order.getDate());
	}
	
	public BigDecimal getIfoodDirectPaymentValue() {
		return ifoodDirectPaymentValue;
	}

	public void setIfoodDirectPaymentValue(BigDecimal ifoodDirectPaymentValue) {
		this.ifoodDirectPaymentValue = ifoodDirectPaymentValue;
	}

	public BigDecimal getIfoodPaymentValue() {
		return ifoodPaymentValue;
	}

	public void setIfoodPaymentValue(BigDecimal ifoodPaymentValue) {
		this.ifoodPaymentValue = ifoodPaymentValue;
	}
	
	public void setIfoodComission (BigDecimal ifoodComission) {
		this.ifoodComission = ifoodComission;
	}

	public BigDecimal getIfoodComission() {
		return ifoodComission;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}
	
	public void setServiceFee(BigDecimal serviceFee) {
		this.ifoodComission = ifoodComission.subtract(new BigDecimal("0.11"));
		this.serviceFee = serviceFee;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void feeForIfood() {
		ifoodComission = this.getOrderValue().multiply(new BigDecimal("0.1720")).setScale(3, RoundingMode.HALF_EVEN);
		ifoodPaymentValue = this.getOrderValue();
	}

	public void feeForStore() {
		ifoodComission = this.getOrderValue().multiply(new BigDecimal("0.12")).setScale(3, RoundingMode.HALF_EVEN);
	}


	public void cutPayments() {
		ifoodPaymentValue = this.getOrderValue();
		BigDecimal orderValue = this.getOrderValue().add(ifoodDirectPaymentValue != null ? ifoodDirectPaymentValue : BigDecimal.ZERO);
		this.setOrderValue(orderValue);
	}
}
