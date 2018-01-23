/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.ReturnExchangeEmailPropertyHelper;
import com.elasticpath.email.handler.producer.impl.AbstractEmailProducer;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * Creates an RMA Receipt {@link Email} when an {@link OrderReturn} has been created.
 */
public class ReturnExchangeEmailProducer extends AbstractEmailProducer {

	private ReturnAndExchangeService returnAndExchangeService;

	private ReturnExchangeEmailPropertyHelper returnExchangeEmailPropertyHelper;

	private EmailComposer emailComposer;

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String UID_KEY = "UID";

	private static final String EMAIL_KEY = "EMAIL";

	@Override
	public Email createEmail(final String guid, final Map<String, Object> emailData) throws EmailException {

		final OrderReturn orderReturn = getOrderReturn(emailData);

		final EmailProperties emailProperties = getReturnExchangeEmailPropertyHelper().getOrderReturnEmailProperties(orderReturn);

		final Email email = getEmailComposer().composeMessage(emailProperties);

		final List<InternetAddress> recipients = getEmailAddress(orderReturn, emailData);
		if (!recipients.isEmpty()) {
			email.setTo(recipients);
		}

		return email;
	}

	/**
	 * Retrieves an {@link OrderReturn} with the given uid.
	 * 
	 * @param emailData email contextual data
	 * @return a {@link OrderReturn}
	 * @throws IllegalArgumentException if an {@link OrderReturn} can not be retrieved from the given parameters
	 */
	protected OrderReturn getOrderReturn(final Map<String, Object> emailData) {

		final Long uid = Long.valueOf((Integer) getObjectFromEmailData(UID_KEY, emailData));
		final List<Long> uids = new ArrayList<>();
		uids.add(uid);

		final List<OrderReturn> orderReturns = getReturnAndExchangeService().findByUids(uids);

		if (orderReturns == null || orderReturns.isEmpty()) {
			throw new IllegalArgumentException("Could not locate a OrderReturn with uid [" + uid + "]");
		}

		return orderReturns.get(0);
	}

	/**
	 * Checks the contextual data for an optional overriding email address.
	 * 
	 * @param orderReturn the order return
	 * @param emailData email contextual data
	 * @return the recipient email address
	 */
	protected List<InternetAddress> getEmailAddress(final OrderReturn orderReturn, final Map<String, Object> emailData) {

		final Object emailValue = emailData.get(EMAIL_KEY);
		final List<InternetAddress> recipients = new ArrayList<>();
		if (emailValue != null) {
			try {
				recipients.add(new InternetAddress(String.valueOf(emailValue)));
			} catch (final AddressException e) {
				throw new IllegalArgumentException("An invalid email address was provided for the notification", e);
			}
		}

		return recipients;
	}

	/**
	 * Retrieves an Object from the given {@code Map} of email contextual data.
	 * 
	 * @param key the object key
	 * @param emailData email contextual data
	 * @return the Object
	 * @throws IllegalArgumentException if the Object can not be retrieved from the given parameters
	 */
	protected Object getObjectFromEmailData(final String key, final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(key) || emailData.get(key) == null) {
			throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE, key));
		}

		return emailData.get(key);
	}

	public void setReturnExchangeEmailPropertyHelper(final ReturnExchangeEmailPropertyHelper orderEmailPropertyHelper) {
		this.returnExchangeEmailPropertyHelper = orderEmailPropertyHelper;
	}

	protected ReturnExchangeEmailPropertyHelper getReturnExchangeEmailPropertyHelper() {
		return returnExchangeEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setReturnAndExchangeService(final ReturnAndExchangeService returnAndExchangeService) {
		this.returnAndExchangeService = returnAndExchangeService;
	}

	public ReturnAndExchangeService getReturnAndExchangeService() {
		return this.returnAndExchangeService;
	}

}
