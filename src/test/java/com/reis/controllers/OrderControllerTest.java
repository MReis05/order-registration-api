package com.reis.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.reis.services.OrderService;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockitoBean
	private OrderService service;
	
	@Test
	@DisplayName("Should return 200 Ok and a List of IfoodOrder")
	void findAllIfoodOrderSuccessCase() throws Exception {
		IfoodOrderResponseDTO dto = new IfoodOrderResponseDTO(createStandardIfoodOrder());
		
		when(service.findAllIfoodOrder()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/orders/ifood")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].orderValue").value(30.00))
				.andExpect(jsonPath("$[0].deliveryValue").value(3.00))
				.andExpect(jsonPath("$[0].paymentMethod").value(PaymentMethod.IFOOD.name()));
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of DirectOrder")
	void findAllDirectOrder() throws Exception {
		DirectOrderResponseDTO dto = new DirectOrderResponseDTO(createStandardDirectOrder());
		
		when(service.findAllDirectOrder()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/orders/direct")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").exists())
				.andExpect(jsonPath("$[0].orderValue").value(30.00))
				.andExpect(jsonPath("$[0].deliveryValue").value(3.00))
				.andExpect(jsonPath("$[0].paymentMethod").value(PaymentMethod.DINHEIRO.name()));
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void saveDirectOrderSuccessCase() throws Exception {
		DirectOrder obj = createStandardDirectOrder();
		DirectOrderRequestDTO inputDTO = new DirectOrderRequestDTO(obj.getOrderValue(), obj.getDeliveryValue(), obj.getPaymentMethod(), obj.getDate());
		DirectOrderResponseDTO outputDTO = new DirectOrderResponseDTO(obj);
		
		when(service.saveDirectOrder(any(DirectOrderRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/orders/direct")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.orderValue").value(30.00))
				.andExpect(jsonPath("$.deliveryValue").value(3.00))
				.andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.DINHEIRO.name()));
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void saveIfoodOrderSuccessCase() throws Exception {
		IfoodOrder order = createStandardIfoodOrder();
		IfoodOrderRequestDTO inputDTO = new IfoodOrderRequestDTO(order.getOrderValue(), order.getDeliveryValue(), PaymentMethod.IFOOD,
				null, null, false, order.getDate());
		IfoodOrderResponseDTO outputDTO = new IfoodOrderResponseDTO(createStandardIfoodOrder());
		
		when(service.saveIfoodOrder(any(IfoodOrderRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/orders/ifood")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.orderValue").value(30.00))
				.andExpect(jsonPath("$.deliveryValue").value(3.00))
				.andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.IFOOD.name()));
	}
	
	private IfoodOrder createStandardIfoodOrder() {
		IfoodOrder obj = new IfoodOrder(createStandardOrder());
		ReflectionTestUtils.setField(obj, "id", 1L);
		obj.setCategory(Category.VIA_IFOOD);
		obj.setType(Type.VIA_IFOOD);
		obj.setPaymentMethod(PaymentMethod.IFOOD);	
		obj.feeForIfood();
		return obj;
	}
	
	private DirectOrder createStandardDirectOrder() {
		DirectOrder obj = new DirectOrder(createStandardOrder());
		ReflectionTestUtils.setField(obj, "id", 1L);
		obj.setType(Type.VIA_PEDIDO_DIRETO);
		obj.setPaymentMethod(PaymentMethod.DINHEIRO);
		return obj;
	}
	
	private Order createStandardOrder() {
		Order obj = new Order();
		obj.setDate(LocalDate.now());
		obj.setOrderValue(new BigDecimal("30.00"));
		obj.setDeliveryValue(new BigDecimal("3.00"));
		return obj;
	}
}
