/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.giftcertificate.producer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.giftcertificate.helper.GiftCertificateEmailPropertyHelper;
import com.elasticpath.email.handler.producer.impl.AbstractEmailProducer;
import com.elasticpath.email.util.EmailComposer;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.OrderService;

/**
 * Creates an Gift Certificate {@link Email} for a purchased gift certificate.
 */
public class GiftCertificateEmailProducer extends AbstractEmailProducer {

	private GiftCertificateEmailPropertyHelper giftCertificateEmailPropertyHelper;
	private EmailComposer emailComposer;
	private OrderService orderService;
	private ProductSkuLookup productSkuLookup;

	private static final String EMAIL_KEY = "emailAddress";

	@Override
	public Email createEmail(final String guid, final Map<String, Object> emailData) throws EmailException {
		final Order order = getOrder(emailData);
		final OrderSku orderSku = getOrderSku(order, emailData);
		final String giftCertificateThemeImageFilename = getGiftCertificateThemeImageFilename(order, orderSku, emailData);

		final EmailProperties emailProperties = getGiftCertificateEmailPropertyHelper().getEmailProperties(order, orderSku,
				giftCertificateThemeImageFilename);

		final Email email = getEmailComposer().composeMessage(emailProperties);

		final List<InternetAddress> recipients = getEmailAddress(emailData);
		if (!recipients.isEmpty()) {
			email.setTo(recipients);
		}

		return email;
	}

	/**
	 * Retrieves an {@link Order} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return an {@link Order}
	 * @throws IllegalArgumentException if an {@link Order} can not be retrieved from the given parameters
	 */
	protected Order getOrder(final Map<String, Object> emailData) {
		final String orderNumber = (String) emailData.get("orderGuid");

		if (orderNumber == null) {
			throw new IllegalArgumentException("The emailData must contain a non-null 'orderGuid' value");
		}

		final Order order = getOrderService().findOrderByOrderNumber(orderNumber);

		if (order == null) {
			throw new IllegalArgumentException("Could not locate an Order for Order Number [" + orderNumber + "]");
		}

		return order;
	}

	/**
	 * Retrieves an {@link OrderSku} from the given {@link Order}.
	 * 
	 * @param order the {@link Order}
	 * @param emailData email contextual data
	 * @return an {@link Order}
	 * @throws IllegalArgumentException if an {@link OrderSku} can not be retrieved from the given parameters
	 */
	protected OrderSku getOrderSku(final Order order, final Map<String, Object> emailData) {
		final String orderSkuGuid = (String) emailData.get("orderSkuGuid");

		if (orderSkuGuid == null) {
			throw new IllegalArgumentException("The emailData must contain a non-null 'orderSkuGuid' value");
		}

		final OrderSku orderSku = order.getOrderSkuByGuid(orderSkuGuid);

		if (orderSku == null) {
			throw new IllegalArgumentException("Could not locate an Order for Order Sku [" + orderSku + "]");
		}

		return orderSku;
	}

	/**
	 * Checks the contextual data for an optional overriding email address.
	 * 
	 * @param emailData email contextual data
	 * @return the recipient email address
	 */
	protected List<InternetAddress> getEmailAddress(final Map<String, Object> emailData) {
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
	 * Retrieves the Gift Certificate theme image filename from the given {@link OrderSku}.
	 *
	 * @param order     the {@link Order}
	 * @param orderSku  the {@link OrderSku}
	 * @param emailData email contextual data
	 * @return a String representing the Gift Certificate theme image filename
	 */
	protected String getGiftCertificateThemeImageFilename(final Order order, final OrderSku orderSku, final Map<String, Object> emailData) {
		final ProductSku sku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
		return sku.getImage();
	}

	public void setGiftCertificateEmailPropertyHelper(final GiftCertificateEmailPropertyHelper giftCertificateEmailPropertyHelper) {
		this.giftCertificateEmailPropertyHelper = giftCertificateEmailPropertyHelper;
	}

	protected GiftCertificateEmailPropertyHelper getGiftCertificateEmailPropertyHelper() {
		return giftCertificateEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	protected EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	protected OrderService getOrderService() {
		return orderService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
