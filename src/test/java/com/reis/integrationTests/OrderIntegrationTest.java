package com.reis.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reis.OrderRegistrationApiApplication;
import com.reis.entities.DirectOrder;
import com.reis.entities.IfoodOrder;
import com.reis.entities.Order;
import com.reis.entities.DTOs.DirectOrderRequestDTO;
import com.reis.entities.DTOs.IfoodOrderRequestDTO;
import com.reis.entities.enums.Category;
import com.reis.entities.enums.PaymentMethod;
import com.reis.entities.enums.Type;
import com.reis.repositories.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = OrderRegistrationApiApplication.class)
@Transactional
public class OrderIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private OrderRepository repository;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Long ifoodOrderId;
	
	private Long directOrderId;
	
	@BeforeEach
	void injectObjects() {
		IfoodOrder ifoodOrder = createStandardIfoodOrder();
		DirectOrder directOrder = createStandardDirectOrder();
		
		ifoodOrder = repository.save(ifoodOrder);
		directOrder = repository.save(directOrder);
		
		this.ifoodOrderId = ifoodOrder.getId();
		this.directOrderId = directOrder.getId();
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of IfoodOrder (End-to-End)")
	void findAllIfoodOrderSuccessCase() throws Exception {
		mockMvc.perform(
				get("/orders/ifood")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(ifoodOrderId))
				.andExpect(jsonPath("$[0].orderValue").value(30.00))
				.andExpect(jsonPath("$[0].deliveryValue").value(3.00))
				.andExpect(jsonPath("$[0].paymentMethod").value(PaymentMethod.IFOOD.name()));
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of DirectOrder (End-to-End)")
	void findAllDirectOrderSuccessCase() throws Exception {
		mockMvc.perform(
				get("/orders/direct")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(directOrderId))
				.andExpect(jsonPath("$[0].orderValue").value(30.00))
				.andExpect(jsonPath("$[0].deliveryValue").value(3.00))
				.andExpect(jsonPath("$[0].paymentMethod").value(PaymentMethod.DINHEIRO.name()));
	}
	
	@Test
	@DisplayName("Should create a DirectOrder in database and return 201 Created (End-to-End)")
	void saveDirectOrderSuccessCase() throws Exception {
		DirectOrderRequestDTO inputDTO = new DirectOrderRequestDTO(new BigDecimal("20.00"), new BigDecimal("4.00"),
				PaymentMethod.CARTÃO, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/orders/direct")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderValue").value(20.00))
				.andExpect(jsonPath("$.deliveryValue").value(4.00))
				.andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.CARTÃO.name()));
		
		DirectOrder savedOrder = (DirectOrder) repository.findAllByType(Type.VIA_PEDIDO_DIRETO).stream()
				.filter(o -> o.getPaymentMethod().equals(PaymentMethod.CARTÃO)).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 3);
		assertEquals(inputDTO.orderValue(), savedOrder.getOrderValue());
		assertEquals(inputDTO.deliveryValue(), savedOrder.getDeliveryValue());
		assertEquals(inputDTO.method(), savedOrder.getPaymentMethod());
		assertEquals(inputDTO.date(), savedOrder.getDate());
	}
	
	@Test
	@DisplayName("Should return 422 Unprocessable Entity when any field isn't valid")
	void saveDirectOrderValidationsExceptionCase() throws Exception {
		DirectOrderRequestDTO inputDTO = new DirectOrderRequestDTO(null, new BigDecimal("4.00"),
				PaymentMethod.CARTÃO, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/orders/direct")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.errors").isArray());
		
		assertEquals(repository.count(), 2);
	}
	
	@Test
	@DisplayName("Should create a IfoodOrder in database and return 201 Created (End-to-End)")
	void saveIfoodOrderSuccessCase() throws Exception {
		IfoodOrderRequestDTO inputDTO = new IfoodOrderRequestDTO(new BigDecimal("20.00"), new BigDecimal("4.00"), PaymentMethod.CARTÃO,
				null, false, false, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/orders/ifood")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderValue").value(20.00))
				.andExpect(jsonPath("$.deliveryValue").value(4.00))
				.andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.CARTÃO.name()));
		
		IfoodOrder savedOrder = (IfoodOrder) repository.findAllByType(Type.VIA_IFOOD).stream()
				.filter(o -> o.getPaymentMethod().equals(PaymentMethod.CARTÃO)).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 3);
		assertEquals(inputDTO.orderValue(), savedOrder.getOrderValue());
		assertEquals(inputDTO.deliveryValue(), savedOrder.getDeliveryValue());
		assertEquals(inputDTO.method(), savedOrder.getPaymentMethod());
		assertEquals(inputDTO.date(), savedOrder.getDate());
	}
	
	private IfoodOrder createStandardIfoodOrder() {
		IfoodOrder obj = new IfoodOrder(createStandardOrder());
		obj.setCategory(Category.VIA_IFOOD);
		obj.setType(Type.VIA_IFOOD);
		obj.setPaymentMethod(PaymentMethod.IFOOD);	
		obj.feeForIfood();
		return obj;
	}
	
	private DirectOrder createStandardDirectOrder() {
		DirectOrder obj = new DirectOrder(createStandardOrder());
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
