package com.reis.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reis.entities.DirectOrder;
import com.reis.entities.IfoodOrder;
import com.reis.entities.Order;
import com.reis.entities.DTOs.DirectOrderRequestDTO;
import com.reis.entities.DTOs.DirectOrderResponseDTO;
import com.reis.entities.DTOs.IfoodOrderRequestDTO;
import com.reis.entities.DTOs.IfoodOrderResponseDTO;
import com.reis.entities.enums.Category;
import com.reis.entities.enums.PaymentMethod;
import com.reis.entities.enums.Type;
import com.reis.repositories.OrderRepository;

@Service
public class OrderService {

	private final OrderRepository repository;

	OrderService(OrderRepository repository) {
		this.repository = repository;
	}
	
	@Transactional(readOnly = true)
	public List<IfoodOrderResponseDTO> findAllIfoodOrder(){
		return repository.findAllByType(Type.VIA_IFOOD)
				.stream()
				.map(order -> new IfoodOrderResponseDTO((IfoodOrder) order))
				.toList();
	}
	
	@Transactional(readOnly = true)
	public List<DirectOrderResponseDTO> findAllDirectOrder(){
		return repository.findAllByType(Type.VIA_PEDIDO_DIRETO)
				.stream()
				.map(order -> new DirectOrderResponseDTO((DirectOrder) order))
				.toList();
	}
	
	@Transactional
	public DirectOrderResponseDTO saveDirectOrder(DirectOrderRequestDTO dto) {
		LocalDate date;
		if(dto.date() != null) {
			date = dto.date();
		}
		else {
			date = LocalDate.now();
		}
		DirectOrder order = new DirectOrder(new Order(dto.orderValue(), dto.deliveryValue(), dto.method(), Type.VIA_PEDIDO_DIRETO, date));
		
		order = repository.save(order);
		
		return new DirectOrderResponseDTO(order);
	}
	
	@Transactional
	public IfoodOrderResponseDTO saveIfoodOrder(IfoodOrderRequestDTO dto) {
		LocalDate date;
		if(dto.date() != null) {
			date = dto.date();
		}
		else {
			date = LocalDate.now();
		}
		
		IfoodOrder order = instatiateIfoodOrder(dto);
		order.setDate(date);
		
		order = repository.save(order);
		
		return new IfoodOrderResponseDTO(order);
	}
	
	private IfoodOrder instatiateIfoodOrder(IfoodOrderRequestDTO dto) {
		IfoodOrder order = new IfoodOrder();
		order.setOrderValue(dto.orderValue());
		order.setDeliveryValue(dto.deliveryValue());
		order.setPaymentMethod(dto.method());
		order.setType(Type.VIA_IFOOD);
		
		if(dto.method() == PaymentMethod.IFOOD) {
			order.setCategory(Category.VIA_IFOOD);
		}
		else {
			order.setCategory(Category.VIA_LOJA);
			if(dto.isSplitPayment()) {
				order.setIfoodDirectPaymentValue(dto.paymentValue());
				order.cutPayments();
			}
			else {
				order.setIfoodDirectPaymentValue(dto.orderValue());
			}
		}
		
		if(order.getCategory() == Category.VIA_IFOOD) {
			order.feeForIfood();
		}
		else {
			order.feeForStore();
		}
		
		if(dto.doesHaveServiceFee()) {
			order.setServiceFee(new BigDecimal("0.99"));
		}
		return order;
	}
}
