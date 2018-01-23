/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.ReturnExchangeEmailPropertyHelper;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * Test class for {@link ReturnExchangeEmailProducer}.
 */
public class ReturnExchangeEmailProducerTest {

	private ReturnExchangeEmailProducer emailProducer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ReturnAndExchangeService returnAndExchangeService = context.mock(ReturnAndExchangeService.class);

	private final ReturnExchangeEmailPropertyHelper returnExchangeEmailPropertyHelper = context.mock(ReturnExchangeEmailPropertyHelper.class);

	private final EmailComposer emailComposer = context.mock(EmailComposer.class);

	private static final String UID_KEY = "UID";

	@Before
	public void setUp() {
		emailProducer = new ReturnExchangeEmailProducer();
		emailProducer.setEmailComposer(emailComposer);
		emailProducer.setReturnExchangeEmailPropertyHelper(returnExchangeEmailPropertyHelper);
		emailProducer.setReturnAndExchangeService(returnAndExchangeService);
	}

	@Test
	public void testVerifyThatReturnExchangeEmailCreatedFromUid() throws Exception {

		final Email expectedEmail = new SimpleEmail();
		final OrderReturn orderReturn = context.mock(OrderReturn.class);

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				final List<Long> uids = new ArrayList<>();
				uids.add(1L);
				allowing(returnAndExchangeService).findByUids(with(equal(uids)));

				final List<OrderReturn> orderReturns = new ArrayList<>();
				orderReturns.add(orderReturn);
				will(returnValue(orderReturns));

				allowing(orderReturn).getPhysicalReturn();
				will(returnValue(true));

				allowing(returnExchangeEmailPropertyHelper).getOrderReturnEmailProperties(orderReturn);
				will(returnValue(emailProperties));

				allowing(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> emailData = createValidAdditionalData();
		final Email actualEmail = emailProducer.createEmail(null, emailData);
		assertSame("Unexpected Email instance produced", expectedEmail, actualEmail);
	}

	@Test(expected = IllegalArgumentException.class)
	public void verifyExceptionIsThrownWhenNoOrderReturnUid() throws Exception {
		final Map<String, Object> emailData = createValidAdditionalData();
		emailData.remove(UID_KEY);

		emailProducer.createEmail(null, emailData);
	}

	@Test(expected = EmailException.class)
	public void verifyEmailExceptionNotCaught() throws Exception {

		final OrderReturn orderReturn = context.mock(OrderReturn.class);

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				final List<Long> uids = new ArrayList<>();
				uids.add(1L);
				final List<OrderReturn> orderReturns = new ArrayList<>();
				orderReturns.add(orderReturn);
				allowing(returnAndExchangeService).findByUids(with(equal(uids)));
				will(returnValue(orderReturns));

				allowing(orderReturn).getPhysicalReturn();
				will(returnValue(true));

				allowing(returnExchangeEmailPropertyHelper).getOrderReturnEmailProperties(orderReturn);
				will(returnValue(emailProperties));

				allowing(emailComposer).composeMessage(emailProperties);
				will(throwException(new EmailException("Boom!")));
			}
		});

		final Map<String, Object> emailData = createValidAdditionalData();
		emailProducer.createEmail(null, emailData);
	}

	@Test
	public void testVerifyThatReturnExchangeEmailCreatedWhenPhysicalReturnIsNotRequired() throws Exception {

		final Email expectedEmail = new SimpleEmail();
		final OrderReturn orderReturn = context.mock(OrderReturn.class);

		context.checking(new Expectations() {
			{
				final EmailProperties emailProperties = context.mock(EmailProperties.class);

				final List<Long> uids = new ArrayList<>();
				uids.add(1L);
				allowing(returnAndExchangeService).findByUids(with(equal(uids)));

				final List<OrderReturn> orderReturns = new ArrayList<>();
				orderReturns.add(orderReturn);
				will(returnValue(orderReturns));

				allowing(orderReturn).getPhysicalReturn();
				will(returnValue(false));

				allowing(returnExchangeEmailPropertyHelper).getOrderReturnEmailProperties(orderReturn);
				will(returnValue(emailProperties));

				allowing(emailComposer).composeMessage(emailProperties);
				will(returnValue(expectedEmail));
			}
		});

		final Map<String, Object> emailData = createValidAdditionalData();
		final Email actualEmail = emailProducer.createEmail(null, emailData);
		assertSame("Unexpected Email instance produced", expectedEmail, actualEmail);
	}

	private Map<String, Object> createValidAdditionalData() {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(UID_KEY, 1);
		return additionalData;
	}

}
