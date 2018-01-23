/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.transformer;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.purchases.PaymentMeansCreditCardEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;

/**
 * Test class for {@link com.elasticpath.rest.resource.purchases.paymentmeans.transformer.PaymentMeansTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PaymentMeansTransformerTest {
	private static final String PURCHASE_URI = "/purchase/uri";
	private static final String PAYMENT_ID = "20000";

	private PaymentMeansResourceLinkFactory paymentMeansResourceLinkFactory;
	private PaymentMeansTransformer paymentMeansTransformer;

	/**
	 * Set up common components and expectations for test.
	 */
	@Before
	public void setUpComponentsAndCommonExpectations() {
		paymentMeansResourceLinkFactory = new PaymentMeansResourceLinkFactory();
		paymentMeansTransformer = new PaymentMeansTransformer(paymentMeansResourceLinkFactory
		);
	}

	/**
	 * Test transforming from {@link PaymentMeansEntity} to a payment means {@link ResourceState} is successful.
	 */
	@Test
	public void testTransformToRepresentation() {
		PaymentMeansEntity paymentMeansDto = ResourceTypeFactory.createResourceEntity(PaymentMeansCreditCardEntity.class);
		ResourceState<PaymentMeansEntity> representation =
				paymentMeansTransformer.transformToRepresentation(paymentMeansDto, PAYMENT_ID, PURCHASE_URI);

		Self expectedSelf = paymentMeansResourceLinkFactory.createPaymentMeansSelf(PURCHASE_URI, PAYMENT_ID, null);
		ResourceLink expectedPaymentMeansListLink = paymentMeansResourceLinkFactory.createPaymentMeansListsLinkForPayment(PURCHASE_URI);
		ResourceLink expectedPurchaseLink = paymentMeansResourceLinkFactory.createPurchaseLinkForPaymentMeans(PURCHASE_URI);

		assertResourceState(representation)
				.self(expectedSelf)
				.containsLinks(Arrays.asList(expectedPaymentMeansListLink, expectedPurchaseLink));
	}
}
