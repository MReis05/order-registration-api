package com.reis.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.entities.DTOs.DirectOrderRequestDTO;
import com.reis.entities.DTOs.DirectOrderResponseDTO;
import com.reis.entities.DTOs.IfoodOrderRequestDTO;
import com.reis.entities.DTOs.IfoodOrderResponseDTO;
import com.reis.services.OrderService;

import jakarta.validation.Valid;

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
	
	@PostMapping("/ifood")
	public ResponseEntity<IfoodOrderResponseDTO> saveIfoodOrder(@Valid @RequestBody IfoodOrderRequestDTO dto){
		IfoodOrderResponseDTO resp = service.saveIfoodOrder(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.id()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PostMapping("/direct")
	public ResponseEntity<DirectOrderResponseDTO> saveDirectOrder(@Valid @RequestBody DirectOrderRequestDTO dto){
		DirectOrderResponseDTO resp = service.saveDirectOrder(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.id()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping("/ifood/{id}")
	public ResponseEntity<IfoodOrderResponseDTO> updateIfoodOrder(@PathVariable Long id, @Valid @RequestBody IfoodOrderRequestDTO dto){
		IfoodOrderResponseDTO resp = service.updateIfoodOrder(id, dto);
		return ResponseEntity.ok().body(resp);
	}
	
	@PutMapping("/direct/{id}")
	public ResponseEntity<DirectOrderResponseDTO> updateDirectOrder(@PathVariable Long id, @Valid @RequestBody DirectOrderRequestDTO dto){
		DirectOrderResponseDTO resp = service.updateDirectOrder(id, dto);
		return ResponseEntity.ok().body(resp);
	}
}