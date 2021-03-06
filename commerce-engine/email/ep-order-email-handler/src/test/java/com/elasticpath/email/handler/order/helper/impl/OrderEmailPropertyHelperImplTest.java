/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.helper.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.domain.impl.EmailPropertiesImpl;
import com.elasticpath.sellingchannel.presentation.OrderPresentationHelper;
import com.elasticpath.service.store.StoreService;

public class OrderEmailPropertyHelperImplTest {
	private OrderEmailPropertyHelperImpl helper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OrderPresentationHelper orderPresentationHelper;
	private StoreService storeService;
	private Store store;
	private Order order;
	private BeanFactory beanFactory;

	@Before
	public void setUp() {
		store = new StoreImpl();
		store.setCode("store");
		store.setStoreAdminEmailAddress("barney.rubble@flintstones.com");

		order = new OrderImpl();
		order.setStoreCode(store.getCode());

		beanFactory = context.mock(BeanFactory.class);
		orderPresentationHelper = context.mock(OrderPresentationHelper.class);
		storeService = context.mock(StoreService.class);

		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));

			allowing(beanFactory).getBean(ContextIdNames.EMAIL_PROPERTIES);
			will(returnValue(new EmailPropertiesImpl()));
		} });

		helper = new OrderEmailPropertyHelperImpl();
		helper.setBeanFactory(beanFactory);
		helper.setOrderPresentationHelper(orderPresentationHelper);
		helper.setStoreService(storeService);
	}

	@Test
	public void testGetFailedShipmentPaymentEmailProperties() {
		OrderShipment shipment = new PhysicalOrderShipmentImpl();
		shipment.setOrder(order);

		EmailProperties props = helper.getFailedShipmentPaymentEmailProperties(shipment, "Oh noze!");
		assertEquals("Store code should be populated", store.getCode(), props.getStoreCode());
		assertEquals("Should send email to store admin", store.getStoreAdminEmailAddress(), props.getRecipientAddress());
	}
}
