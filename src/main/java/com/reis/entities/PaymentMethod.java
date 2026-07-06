package com.reis.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentMethod {

	@Column(name = "payment_method_type")
	private String paymentMethod;
	private Double paymentValue;
	
	public PaymentMethod() {
	}
	
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod, Double value) {
		setPaymentValue(value);
		this.paymentMethod = paymentMethod;
	}

	public Double getPaymentValue() {
		return paymentValue;
	}

	public void setPaymentValue(Double paymentValue) {
		this.paymentValue = paymentValue;
	}
}
