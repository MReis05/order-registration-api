package com.reis.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.reis.entities.DirectOrder;
import com.reis.entities.IfoodOrder;
import com.reis.entities.Order;
import com.reis.entities.enums.Category;
import com.reis.entities.enums.PaymentMethod;
import com.reis.entities.enums.Type;
import com.reis.repositories.OrderRepository;

@Configuration
public class TestConfig implements CommandLineRunner {

	private final OrderRepository repository;

	TestConfig(OrderRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void run(String... args) throws Exception {
		DirectOrder d01 = createStandardDirectOrder();
		DirectOrder d02= createStandardDirectOrder();
		d02.setOrderValue(new BigDecimal("25.00"));
		d02.setPaymentMethod(PaymentMethod.CARTÃO);
		
		IfoodOrder i01 = createStandardIfoodOrder();
		i01.setPaymentMethod(PaymentMethod.IFOOD);
		i01.feeForIfood();
		IfoodOrder i02 = createStandardIfoodOrder();
		i02.setOrderValue(new BigDecimal("25.00"));
		i02.setPaymentMethod(PaymentMethod.CARTÃO);
		i02.setCategory(Category.VIA_LOJA);
		i02.setIfoodDirectPaymentValue(i02.getOrderValue());
		i02.feeForStore();
		
		repository.saveAll(Arrays.asList(d01, d02, i01, i02));
	}
	
	private IfoodOrder createStandardIfoodOrder() {
		IfoodOrder obj = new IfoodOrder(createStandardOrder());
		obj.setCategory(Category.VIA_IFOOD);
		obj.setType(Type.VIA_IFOOD);
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
