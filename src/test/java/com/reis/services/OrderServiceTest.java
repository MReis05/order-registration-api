package com.reis.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.reis.entities.DTOs.DirectOrderRequestDTO;
import com.reis.entities.DTOs.DirectOrderResponseDTO;
import com.reis.entities.DTOs.IfoodOrderRequestDTO;
import com.reis.entities.DTOs.IfoodOrderResponseDTO;
import com.reis.entities.enums.Category;
import com.reis.entities.enums.PaymentMethod;
import com.reis.entities.enums.Type;
import com.reis.repositories.OrderRepository;
import com.reis.services.exceptions.ResourceNotFoundException;

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
	
	@Test
	@DisplayName("Should save DirectOrder and return DirectOrderResponseDTO")
	void saveDirectOrderSuccessCase() {
		DirectOrder order = createStandardDirectOrder();
		DirectOrderRequestDTO dto = new DirectOrderRequestDTO(order.getOrderValue(), order.getDeliveryValue(), order.getPaymentMethod(), order.getDate());
		
		when(repository.save(any(Order.class))).thenAnswer(invocation ->{
			DirectOrder o = invocation.getArgument(0);
			ReflectionTestUtils.setField(o, "id", 1L);
			return o;
		});
		
		DirectOrderResponseDTO orderReceived = service.saveDirectOrder(dto);
		
		assertNotNull(orderReceived);
		assertEquals(dto.orderValue(), orderReceived.orderValue());
		assertEquals(dto.deliveryValue(), orderReceived.deliveryValue());
		assertEquals(dto.method(), orderReceived.paymentMethod());
		assertEquals(dto.date(), orderReceived.date());
		
		verify(repository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Should save IfoodOrder and return IfoodOrderResponseDTO")
	void saveIfoodOrderSuccessCase() {
		IfoodOrder order = createStandardIfoodOrder();
		IfoodOrderRequestDTO dto = new IfoodOrderRequestDTO(order.getOrderValue(), order.getDeliveryValue(), PaymentMethod.IFOOD,
				null, false, false, order.getDate());
		
		when(repository.save(any(Order.class))).thenAnswer(invocation ->{
			IfoodOrder o = invocation.getArgument(0);
			ReflectionTestUtils.setField(o, "id", 1L);
			return o;
		});
		
		IfoodOrderResponseDTO orderReceived = service.saveIfoodOrder(dto);
		
		assertNotNull(orderReceived);
		assertEquals(dto.orderValue(), orderReceived.orderValue());
		assertEquals(dto.deliveryValue(), orderReceived.deliveryValue());
		assertEquals(dto.method(), orderReceived.paymentMethod());
		assertEquals(dto.date(), orderReceived.date());
		
		verify(repository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Should update DirectOrder and return DirectOrderResponseDTO")
	void updateDirectOrderSuccessCase() {
		Long id = 1L;
		DirectOrder order = createStandardDirectOrder();
		
		when(repository.findById(id)).thenReturn(Optional.of(order));
		
		DirectOrderRequestDTO dto = new DirectOrderRequestDTO(new BigDecimal("40.00"), order.getDeliveryValue(), order.getPaymentMethod(), order.getDate());
		
		when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		DirectOrderResponseDTO orderReceived = service.updateDirectOrder(id, dto);
		
		assertNotNull(orderReceived);
		assertEquals(dto.orderValue(), orderReceived.orderValue());
		assertEquals(dto.deliveryValue(), orderReceived.deliveryValue());
		assertEquals(dto.method(), orderReceived.paymentMethod());
		assertEquals(dto.date(), orderReceived.date());
		
		verify(repository).findById(id);
		verify(repository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void updateDirectOrderResourceNotFoundCase() {
		Long id = 99L;
		DirectOrder order = createStandardDirectOrder();
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		DirectOrderRequestDTO dto = new DirectOrderRequestDTO(new BigDecimal("40.00"), order.getDeliveryValue(), order.getPaymentMethod(), order.getDate());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->{
			service.updateDirectOrder(id, dto);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(id);
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should update IfoodOrder and return IfoodOrderResponseDTO")
	void updateIfoodOrderSuccessCase() {
		Long id = 1L;
		IfoodOrder order = createStandardIfoodOrder();
		
		when(repository.findById(id)).thenReturn(Optional.of(order));
		
		IfoodOrderRequestDTO dto = new IfoodOrderRequestDTO(order.getOrderValue(), order.getDeliveryValue(), PaymentMethod.IFOOD,
				null, false, false, order.getDate());
		
		when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		IfoodOrderResponseDTO orderReceived = service.updateIfoodOrder(id, dto);
		
		assertNotNull(orderReceived);
		assertEquals(dto.orderValue(), orderReceived.orderValue());
		assertEquals(dto.deliveryValue(), orderReceived.deliveryValue());
		assertEquals(dto.method(), orderReceived.paymentMethod());
		assertEquals(dto.date(), orderReceived.date());
		
		verify(repository).findById(id);
		verify(repository).save(any(Order.class));
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void updateIfoodOrderResourceNotFoundCase() {
		Long id = 99L;
		IfoodOrder order = createStandardIfoodOrder();
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		IfoodOrderRequestDTO dto = new IfoodOrderRequestDTO(order.getOrderValue(), order.getDeliveryValue(), PaymentMethod.IFOOD,
				null, false, false, order.getDate());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->{
			service.updateIfoodOrder(id, dto);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(id);
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should delete Order when id exists")
	void deleteSuccessCase() {
		Long id = 1L;
		
		when(repository.existsById(id)).thenReturn(true);
		
		service.deleteOrder(id);
		
		verify(repository).existsById(id);
		verify(repository).deleteById(id);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void deleteResourceNotFoundCase() {
		Long id = 99L;
		
		when(repository.existsById(id)).thenReturn(false);
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->{
			service.deleteOrder(id);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).existsById(id);
		verify(repository, never()).deleteById(id);
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
