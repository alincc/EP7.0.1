/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.transformer.handler.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PaymentMeansPaymentTokenEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Implementation of {@link PaymentHandler} that handles {@link PaymentMeansPaymentTokenEntity}s.
 */
@Singleton
@Named("paymentTokenPaymentMeansHandler")
public class PaymentTokenPaymentMeansHandler implements PaymentHandler {
	@Override
	public Class<? extends ResourceEntity> handledType() {
		return PaymentMeansPaymentTokenEntity.class;
	}

	@Override
	public String representationType() {
		return PurchasesMediaTypes.PAYMENT_MEANS_PAYMENT_TOKEN.id();
	}

}
