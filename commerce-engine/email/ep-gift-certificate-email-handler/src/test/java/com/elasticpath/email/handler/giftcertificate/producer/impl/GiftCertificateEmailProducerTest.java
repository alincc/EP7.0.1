/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.giftcertificate.producer.impl;

import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.giftcertificate.helper.GiftCertificateEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.OrderService;

/**
 * Test class for {@link com.elasticpath.email.handler.giftcertificate.producer.impl.GiftCertificateEmailProducer}.
 */
public class GiftCertificateEmailProducerTest {

	private static final String GIFT_CERTIFICATE_GUID = "ABCD-1234-FGHI-5678";

	private static final String ORDER_GUID = "200000";

	private static final String ORDER_SKU_GUID = "ZYXW-8765-VUTS-4321";

	private GiftCertificateEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final GiftCertificateEmailPropertyHelper giftCertificateEmailPropertyHelper = context.mock(GiftCertificateEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);
	private final OrderService orderService = context.mock(OrderService.class);
	private final ProductSkuLookup productSkuLookup = context.mock(ProductSkuLookup.class);

	@Before
	public void setUp() {
		emailProducer = new GiftCertificateEmailProducer();
		emailProducer.setGiftCertificateEmailPropertyHelper(giftCertificateEmailPropertyHelper);
		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setOrderService(orderService);
		emailProducer.setProductSkuLookup(productSkuLookup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionIsThrownWhenNoOrderGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap();
		emailDataMap.put("orderGuid", null);

		emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionIsThrownWhenNoOrderSkuGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap();
		emailDataMap.put("orderSkuGuid", null);

		givenOrderServiceFindsOrder(context.mock(Order.class));

		emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionIsThrownWhenNoOrderMatchingOrderGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap();

		givenOrderServiceFindsOrder(null);

		emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionIsThrownWhenNoOrderSkuMatchingOrderSkuGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap();

		final Order order = context.mock(Order.class);

		givenOrderServiceFindsOrder(order);
		givenOrderFindsOrderSkuForGuid(order, null);

		emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {
		final Map<String, Object> emailData = createEmailDataMap();

		final String giftCertificateThemeImageFilename = "hello.jpg";
		final OrderSku orderSku = createOrderSku(giftCertificateThemeImageFilename);
		final Order order = context.mock(Order.class);

		givenOrderServiceFindsOrder(order);
		givenOrderFindsOrderSkuForGuid(order, orderSku);

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				allowing(giftCertificateEmailPropertyHelper).getEmailProperties(order, orderSku, giftCertificateThemeImageFilename);
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Boom!")));
			}
		});

		emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailData);
	}

	@Test
	public void testVerifyThatGiftCertificateEmailIsConstructedForOrderAndOrderSku() throws Exception {
		final Map<String, Object> emailData = createEmailDataMap();

		final String giftCertificateThemeImageFilename = "hello.jpg";
		final OrderSku orderSku = createOrderSku(giftCertificateThemeImageFilename);
		final Order order = context.mock(Order.class);

		final Email expectedEmail = new SimpleEmail();

		givenOrderServiceFindsOrder(order);
		givenOrderFindsOrderSkuForGuid(order, orderSku);

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				oneOf(giftCertificateEmailPropertyHelper).getEmailProperties(order, orderSku, giftCertificateThemeImageFilename);
				will(returnValue(emailProperties));

				oneOf(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Email actualEmail = emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailData);
		assertSame("Unexpected Email instance produced", expectedEmail, actualEmail);
	}

	private Map<String, Object> createEmailDataMap() {
		final Map<String, Object> emailDataMap = new HashMap<>(2);
		emailDataMap.put("orderGuid", ORDER_GUID);
		emailDataMap.put("orderSkuGuid", ORDER_SKU_GUID);
		return emailDataMap;
	}

	private OrderSku createOrderSku(final String giftCertificateThemeImageFilename) {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		productSku.setImage(giftCertificateThemeImageFilename);

		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setSkuGuid(productSku.getGuid());

		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findByGuid(productSku.getGuid());
				will(returnValue(productSku));
			}
		});
		return orderSku;
	}

	private void givenOrderServiceFindsOrder(final Order order) {
		context.checking(new Expectations() {
			{
				oneOf(orderService).findOrderByOrderNumber(ORDER_GUID);
				will(returnValue(order));
			}
		});
	}

	private void givenOrderFindsOrderSkuForGuid(final Order order, final OrderSku orderSku) {
		context.checking(new Expectations() {
			{
				oneOf(order).getOrderSkuByGuid(ORDER_SKU_GUID);
				will(returnValue(orderSku));
			}
		});
	}

}
