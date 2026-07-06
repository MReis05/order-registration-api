package com.reis.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reis.entities.DTOs.DirectOrderResponseDTO;
import com.reis.entities.DTOs.IfoodOrderResponseDTO;
import com.reis.services.OrderService;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

	private final OrderService service;
	
	OrderController(OrderService service){
		this.service = service;
	}
	
	@GetMapping("/ifood")
	public ResponseEntity<List<IfoodOrderResponseDTO>> findAllIfoodOrder(){
		List<IfoodOrderResponseDTO> list = service.findAllIfoodOrder();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/direct")
	public ResponseEntity<List<DirectOrderResponseDTO>> findAllDirectOrder(){
		List<DirectOrderResponseDTO> list = service.findAllDirectOrder();
		return ResponseEntity.ok().body(list);
	}
}
