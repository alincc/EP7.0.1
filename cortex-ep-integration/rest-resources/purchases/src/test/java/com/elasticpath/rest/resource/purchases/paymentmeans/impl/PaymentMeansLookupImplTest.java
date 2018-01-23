/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.impl;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.resource.commons.handler.registry.PaymentHandlerRegistry;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansResourceLinkFactory;
import com.elasticpath.rest.resource.purchases.paymentmeans.integration.PaymentMeansLookupStrategy;
import com.elasticpath.rest.resource.purchases.paymentmeans.transformer.PaymentMeansTransformer;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link PaymentMeansLookupImpl} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PaymentMeansTransformer.class)
public class PaymentMeansLookupImplTest {

	public static final String SCOPE = "scope";
	public static final String PURCHASE_ID = Base32Util.encode("purchaseId");
	public static final String PURCHASE_URI = "/purchaseUri";
	public static final String PAYMENT_MEANS_ID = "paymentMeansId";
	public static final String TEST_PAYMENT_MEAN_ID = "testPaymentMeanId";
	public static final String SELF_URI = "/selfUri";

	@Mock
	private PaymentHandler paymentHandler;
	@Mock
	private PaymentMeansTransformer paymentMeansTransformer;
	@Mock
	private PaymentMeansLookupStrategy paymentMeansLookupStrategy;
	@Mock
	private PaymentHandlerRegistry paymentMeansHandlerRegistry;

	private PaymentMeansResourceLinkFactory paymentMeansResourceLinkFactory;

	private PaymentMeansLookupImpl paymentMeansLookup;

	/**
	 * Set up common components and common expectations for the test.
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUpTestComponentsAndCommonExpectations() {
		paymentMeansResourceLinkFactory = new PaymentMeansResourceLinkFactory();

		Class paymentMeansDtoClass = PaymentMeansEntity.class;
		when(paymentMeansHandlerRegistry.lookupHandler(any(PaymentMeansEntity.class))).thenReturn(paymentHandler);
		when(paymentHandler.representationType()).thenReturn(PurchasesMediaTypes.PAYMENT_MEANS.id());
		when(paymentHandler.handledType()).thenReturn(paymentMeansDtoClass);

		paymentMeansLookup = new PaymentMeansLookupImpl(paymentMeansLookupStrategy, paymentMeansTransformer,
				paymentMeansResourceLinkFactory, paymentMeansHandlerRegistry);
	}

	/**
	 * Test find payment means ids by purchase id is successful.
	 */
	@Test
	public void testFindPaymentMeansIdsByPurchaseId() {
		ResourceLink expectedPurchaseLinkFromPurchaseUri = paymentMeansResourceLinkFactory.createPurchaseLinkFromPurchaseUri(PURCHASE_URI);
		Self expectedPaymentMeansSelf = paymentMeansResourceLinkFactory.createPaymentMeansSelf(PURCHASE_URI);

		PaymentMeansEntity paymentMeansEntity = PaymentMeansEntity.builder()
				.withPaymentMeansId(PAYMENT_MEANS_ID).build();

		Collection<PaymentMeansEntity> paymentMeansDtos =
				Arrays.asList(paymentMeansEntity);
		shouldGetPurchasePayments(ExecutionResultFactory.createReadOK(paymentMeansDtos));

		ExecutionResult<ResourceState<LinksEntity>> result = paymentMeansLookup.findPaymentMeansIdsByPurchaseId(SCOPE, PURCHASE_ID, PURCHASE_URI);

		assertResourceState(result.getData())
				.self(expectedPaymentMeansSelf)
				.containsLink(expectedPurchaseLinkFromPurchaseUri)
				.containsLink(ElementListFactory.createElementWithRev("/purchaseuri/paymentmeans/obqxs3lfnz2e2zlbnzzusza=",
						PurchasesMediaTypes.PAYMENT_MEANS.id(), "list"));
	}

	/**
	 * Test that the payment means links representation is created correctly.
	 */
	@Test
	public void testHandlePaymentMeansLinksRepresentation() {
		PaymentMeansEntity paymentMeansEntity = PaymentMeansEntity.builder()
				.withPaymentMeansId(TEST_PAYMENT_MEAN_ID).build();

		ResourceLink expectedElementLink = ElementListFactory.createElementOfList(URIUtil.format(
				SELF_URI, PurchaseResourceRels.PAYMENT_MEANS, Base32Util.encode(paymentMeansEntity.getPaymentMeansId())),
				PurchasesMediaTypes.PAYMENT_MEANS.id());

		ResourceState<LinksEntity> actualLinksRepresentation = paymentMeansLookup.getPaymentMeansLinksRepresentation(
				SELF_URI, Arrays.asList(paymentMeansEntity));

		assertResourceState(actualLinksRepresentation)
				.containsLink(expectedElementLink);
	}

	/**
	 * Tests finding a payment mean by id.
	 */
	@Test
	public void testfindPaymentMeansById() {
		PaymentMeansEntity paymentMeansDto = ResourceTypeFactory.createResourceEntity(PaymentMeansEntity.class);
		ResourceState<PaymentMeansEntity> expectedRepresentation =	ResourceState.Builder
				.create(PaymentMeansEntity.builder().build())
				.build();

		shouldGetPurchasePayment(ExecutionResultFactory.createReadOK(paymentMeansDto));
		shouldTransformToRepresentation(expectedRepresentation, paymentMeansDto);

		ExecutionResult<ResourceState<PaymentMeansEntity>> result =
				paymentMeansLookup.findPaymentMeansById(SCOPE, PURCHASE_URI, PURCHASE_ID, PAYMENT_MEANS_ID);

		assertEquals("The representation returned should be the same as expected", expectedRepresentation, result.getData());
	}

	private void shouldGetPurchasePayments(final ExecutionResult<Collection<PaymentMeansEntity>> result) {
		when(paymentMeansLookupStrategy.getPurchasePayments(SCOPE, Base32Util.decode(PURCHASE_ID))).thenReturn(result);
	}

	private void shouldGetPurchasePayment(final ExecutionResult<PaymentMeansEntity> result) {
		when(paymentMeansLookupStrategy.getPurchasePayment(SCOPE, Base32Util.decode(PURCHASE_ID),
				Base32Util.decode(PAYMENT_MEANS_ID))).thenReturn(result);
	}

	private void shouldTransformToRepresentation(final ResourceState<PaymentMeansEntity> representation,
			final PaymentMeansEntity paymentMeansDto) {
		when(paymentMeansTransformer.transformToRepresentation(paymentMeansDto, PAYMENT_MEANS_ID, PURCHASE_URI)).thenReturn(representation);
	}
}
