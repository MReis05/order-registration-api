package com.reis.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import com.reis.entities.enums.PaymentMethod;
import com.reis.entities.enums.Type;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "order_type", discriminatorType = DiscriminatorType.STRING)
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private BigDecimal orderValue;
	private BigDecimal deliveryValue;
	private PaymentMethod paymentMethod;
	private Type type;
	private LocalDate date;

	public Order() {
	}

	public Order(BigDecimal orderValue, BigDecimal deliveryValue, PaymentMethod paymentMethod, Type type, LocalDate date) {
		this.orderValue = orderValue;
		this.deliveryValue = deliveryValue;
		this.paymentMethod = paymentMethod;
		this.type = type;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(BigDecimal orderValue) {
		this.orderValue = orderValue.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getDeliveryValue() {
		return deliveryValue;
	}

	public void setDeliveryValue(BigDecimal deliveryValue) {
		this.deliveryValue = deliveryValue.setScale(2, RoundingMode.HALF_EVEN);
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}
}
