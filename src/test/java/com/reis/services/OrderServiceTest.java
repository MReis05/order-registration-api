package com.reis.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.entities.DirectOrder;
import com.reis.entities.IfoodOrder;
import com.reis.entities.Order;
import com.reis.entities.DTOs.DirectOrderResponseDTO;
import com.reis.entities.DTOs.IfoodOrderResponseDTO;
import com.reis.entities.enums.Category;
import com.reis.entities.enums.PaymentMethod;
import com.reis.entities.enums.Type;
import com.reis.repositories.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@Mock
	private OrderRepository repository;
	
	@InjectMocks
	private OrderService service;
	
	@Test
	@DisplayName("Should return a List<IfoodOrderResponseDTO> when finds objects")
	void findAllIfoodOrderSuccessCase() {
		List<Order> listExpected = List.of(createStandardIfoodOrder());
		
		when(repository.findAllByType(Type.VIA_IFOOD)).thenReturn(listExpected);
		
		List<IfoodOrderResponseDTO> listReceived = service.findAllIfoodOrder();
		
		assertEquals(listExpected.size(), listReceived.size());
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).id());
		assertEquals(listExpected.get(0).getOrderValue(), listReceived.get(0).orderValue());
		assertEquals(listExpected.get(0).getDate(), listReceived.get(0).date());
		assertEquals(listExpected.get(0).getPaymentMethod(), listReceived.get(0).paymentMethod());
		assertEquals(createStandardIfoodOrder().getIfoodComission(), listReceived.get(0).ifoodComission());
		
		verify(repository).findAllByType(Type.VIA_IFOOD);
	}
	
	@Test
	@DisplayName("Should return a List<DirectOrderResponseDTO> when finds objects")
	void findAllDirectOrderSuccessCase() {
		List<Order> listExpected = List.of(createStandardDirectOrder());
		
		when(repository.findAllByType(Type.VIA_PEDIDO_DIRETO)).thenReturn(listExpected);
		
		List<DirectOrderResponseDTO> listReceived = service.findAllDirectOrder();
		
		assertEquals(listExpected.size(), listReceived.size());
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).id());
		assertEquals(listExpected.get(0).getOrderValue(), listReceived.get(0).orderValue());
		assertEquals(listExpected.get(0).getDate(), listReceived.get(0).date());
		assertEquals(listExpected.get(0).getPaymentMethod(), listReceived.get(0).paymentMethod());
		
		verify(repository).findAllByType(Type.VIA_PEDIDO_DIRETO);
	}
	
	private IfoodOrder createStandardIfoodOrder() {
		IfoodOrder obj = new IfoodOrder(createStandardOrder());
		obj.setCategory(Category.VIA_IFOOD);
		obj.setType(Type.VIA_IFOOD);
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
		ReflectionTestUtils.setField(obj, "id", 1L);
		obj.setDate(LocalDate.now());
		obj.setOrderValue(new BigDecimal("30.00"));
		obj.setDeliveryValue(new BigDecimal("3.00"));
		return obj;
	}
}
