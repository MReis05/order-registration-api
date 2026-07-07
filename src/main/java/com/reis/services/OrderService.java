package com.reis.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reis.entities.DirectOrder;
import com.reis.entities.IfoodOrder;
import com.reis.entities.Order;
import com.reis.entities.DTOs.DirectOrderRequestDTO;
import com.reis.entities.DTOs.DirectOrderResponseDTO;
import com.reis.entities.DTOs.IfoodOrderResponseDTO;
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
		DirectOrder order = new DirectOrder(new Order(dto.orderValue(), dto.orderValue(), dto.method(), Type.VIA_PEDIDO_DIRETO, date));
		
		order = repository.save(order);
		
		return new DirectOrderResponseDTO(order);
	}
}
