package com.reis.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
	
	@Test
	@DisplayName("Should return 200 Ok and update DirectOrder in database (End-to-End)")
	void updateDirectOrderSuccessCase() throws Exception {
		DirectOrderRequestDTO inputDTO = new DirectOrderRequestDTO(new BigDecimal("30.00"), new BigDecimal("4.00"),
				PaymentMethod.CARTÃO, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/orders/direct/" + directOrderId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderValue").value(30.00))
				.andExpect(jsonPath("$.deliveryValue").value(4.00))
				.andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.CARTÃO.name()));
		
		DirectOrder savedOrder = (DirectOrder) repository.findAllByType(Type.VIA_PEDIDO_DIRETO).stream()
				.filter(o -> o.getPaymentMethod().equals(PaymentMethod.CARTÃO)).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 2);
		assertEquals(inputDTO.orderValue(), savedOrder.getOrderValue());
		assertEquals(inputDTO.deliveryValue(), savedOrder.getDeliveryValue());
		assertEquals(inputDTO.method(), savedOrder.getPaymentMethod());
		assertEquals(inputDTO.date(), savedOrder.getDate());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't update DirectOrder in database (End-to-End)")
	void updateDirectOrderResourceNotFoundCase() throws Exception {
		DirectOrderRequestDTO inputDTO = new DirectOrderRequestDTO(new BigDecimal("30.00"), new BigDecimal("4.00"),
				PaymentMethod.CARTÃO, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/orders/direct/" + (directOrderId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
		
		DirectOrder savedOrder = (DirectOrder) repository.findAllByType(Type.VIA_PEDIDO_DIRETO).stream()
				.filter(o -> o.getPaymentMethod().equals(PaymentMethod.DINHEIRO)).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 2);
		assertEquals(new BigDecimal("3.00"), savedOrder.getDeliveryValue());
		assertEquals(PaymentMethod.DINHEIRO, savedOrder.getPaymentMethod());
	}
	
	@Test
	@DisplayName("Should return 200 Ok and update IfoodOrder in database (End-to-End)")
	void updateIfoodOrderSuccessCase() throws Exception {
		IfoodOrderRequestDTO inputDTO = new IfoodOrderRequestDTO(new BigDecimal("5.00"), new BigDecimal("4.00"), PaymentMethod.CARTÃO,
				new BigDecimal("15.00"), true, true, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/orders/ifood/" + ifoodOrderId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderValue").value(20.00))
				.andExpect(jsonPath("$.deliveryValue").value(4.00))
				.andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.CARTÃO.name()));
		
		IfoodOrder savedOrder = (IfoodOrder) repository.findAllByType(Type.VIA_IFOOD).stream()
				.filter(o -> o.getPaymentMethod().equals(PaymentMethod.CARTÃO)).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 2);
		assertEquals(new BigDecimal("20.00"), savedOrder.getOrderValue());
		assertEquals(inputDTO.deliveryValue(), savedOrder.getDeliveryValue());
		assertEquals(inputDTO.method(), savedOrder.getPaymentMethod());
		assertEquals(inputDTO.date(), savedOrder.getDate());
		assertEquals(new BigDecimal("5.00"), savedOrder.getIfoodPaymentValue());
		assertEquals(inputDTO.paymentValue(), savedOrder.getIfoodDirectPaymentValue());
		assertEquals(new BigDecimal("0.99"), savedOrder.getServiceFee());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't update IfoodOrder in database (End-to-End)")
	void updateIfoodOrderResourceNotFoundCase() throws Exception {
		IfoodOrderRequestDTO inputDTO = new IfoodOrderRequestDTO(new BigDecimal("20.00"), new BigDecimal("4.00"), PaymentMethod.CARTÃO,
				null, false, false, LocalDate.now());
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/orders/ifood/" + (ifoodOrderId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
		
		IfoodOrder savedOrder = (IfoodOrder) repository.findAllByType(Type.VIA_IFOOD).stream()
				.filter(o -> o.getPaymentMethod().equals(PaymentMethod.IFOOD)).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 2);
		assertEquals(new BigDecimal("30.00"), savedOrder.getOrderValue());
		assertEquals(new BigDecimal("3.00"), savedOrder.getDeliveryValue());
		assertEquals(PaymentMethod.IFOOD, savedOrder.getPaymentMethod());
	}
	
	@Test
	@DisplayName("Should return 204 No Content and delete Order in database (End-to-End)")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/orders/" + directOrderId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
		
		assertEquals(repository.count(), 1);
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't delete Order in database (End-to-End)")
	void deleteResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				delete("/orders/" + (directOrderId + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));

		assertEquals(repository.count(), 2);

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
