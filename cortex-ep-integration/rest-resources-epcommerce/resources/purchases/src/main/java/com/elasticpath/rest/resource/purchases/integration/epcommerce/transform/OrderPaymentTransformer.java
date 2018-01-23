/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definition.purchases.ExpirationDateEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansCreditCardEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansPaymentTokenEntity;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.domain.wrapper.OrderPaymentWrapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms between a {@link OrderPaymentWrapper} and {@link PaymentMeansCreditCardEntity}, and vice versa.
 */
@Singleton
@Named("orderPaymentTransformer")
public class OrderPaymentTransformer extends AbstractDomainTransformer<OrderPaymentWrapper, PaymentMeansEntity> {

	private static final String VOICE_TYPE = "voice";

	@Override
	public OrderPaymentWrapper transformToDomain(final PaymentMeansEntity paymentMeansDto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PaymentMeansEntity transformToEntity(final OrderPaymentWrapper orderPaymentWrapper,
												final Locale locale) {
		OrderPayment orderPayment = orderPaymentWrapper.getOrderPayment();

		PaymentType paymentType = orderPayment.getPaymentMethod();
		if (PaymentType.PAYMENT_TOKEN
				.equals(paymentType)) {
			return PaymentMeansPaymentTokenEntity.builder()
					.withPaymentMeansId(String.valueOf(orderPayment.getUidPk()))
					.withDisplayName(orderPayment.getDisplayValue())
					.build();
		}

		OrderAddress billingAddress = orderPaymentWrapper.getOrderAddress();

		ExpirationDateEntity expirationDateEntity = ExpirationDateEntity.builder()
				.withMonth(orderPayment.getExpiryMonth())
				.withYear(orderPayment.getExpiryYear())
				.build();
		NameEntity nameEntity = NameEntity.Builder
				.builder()
				.withFamilyName(StringUtils.trimToNull(billingAddress.getLastName()))
				.withGivenName(StringUtils.trimToNull(billingAddress.getFirstName()))
				.build();
		AddressDetailEntity addressDetailEntity = AddressDetailEntity.builder()
				.withStreetAddress(StringUtils.trimToNull(billingAddress.getStreet1()))
				.withExtendedAddress(StringUtils.trimToNull(billingAddress.getStreet2()))
				.withLocality(StringUtils.trimToNull(billingAddress.getCity()))
				.withRegion(StringUtils.trimToNull(billingAddress.getSubCountry()))
				.withCountryName(StringUtils.trimToNull(billingAddress.getCountry()))
				.withPostalCode(StringUtils.trimToNull(billingAddress.getZipOrPostalCode()))
				.build();
		AddressEntity billingAddressEntity = AddressEntity.builder()
				.withName(nameEntity)
				.withAddress(addressDetailEntity)
				.build();

		return PaymentMeansCreditCardEntity.builder()
				.withPaymentMeansId(String.valueOf(orderPayment.getUidPk()))
				.withHolderName(orderPayment.getCardHolderName())
				.withPrimaryAccountNumberId(orderPayment.getDisplayValue())
				.withCardType(orderPayment.getCardType())
				.withExpiryDate(expirationDateEntity)
				.withTelephoneNumber(billingAddress.getPhoneNumber())
				.withTelephoneType(VOICE_TYPE)
				.withBillingAddress(billingAddressEntity)
				.build();
	}
}
