/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.transformer;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.PurchasesResourceLinkFactory;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PurchaseListUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseListUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.resource.purchases.constants.PurchaseStatus;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Purchase Transformer test.
 */
public final class PurchaseTransformerTest {

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	/**
	 * Test testTransformToRepresentation.
	 */
	@Test
	public void testTransformToRepresentation() {
		final PurchasesResourceLinkFactory purchasesResourceLinkFactory = new PurchasesResourceLinkFactory();
		final PurchaseListUriBuilderFactory mockPurchaseListUriBuilderFactory = context.mock(PurchaseListUriBuilderFactory.class);

		final String testScope = "test scope";
		final String testResourceServerName = "test resource server name";
		final String purchaseListUri = URIUtil.format(testResourceServerName, testScope);
		context.checking(new Expectations() {
			{
				PurchaseListUriBuilder mockPurchaseListUriBuilder = context.mock(PurchaseListUriBuilder.class);

				allowing(mockPurchaseListUriBuilderFactory).get();
				will(returnValue(mockPurchaseListUriBuilder));

				allowing(mockPurchaseListUriBuilder).setScope(testScope);
				will(returnValue(mockPurchaseListUriBuilder));

				allowing(mockPurchaseListUriBuilder).build();
				will(returnValue(purchaseListUri));
			}
		});

		PurchaseTransformer purchaseTransformer = new PurchaseTransformer(
				testResourceServerName, purchasesResourceLinkFactory, mockPurchaseListUriBuilderFactory);

		String testPurchaseId = "test purchase id";
		String testOrderId = "test order id";
		PurchaseStatus testStatus = PurchaseStatus.COMPLETED;
		PurchaseEntity dto = PurchaseEntity.builder()
				.withOrderId(testOrderId)
				.withPurchaseId(testPurchaseId)
				.withMonetaryTotal(null)
				.withTaxes(null)
				.withTaxTotal(null)
				.withStatus(testStatus.name())
				.build();

		ResourceState<PurchaseEntity> purchase = purchaseTransformer.transformToRepresentation(testScope, dto);

		String expectedSelfUri = "/test resource server name/test scope/" + Base32Util.encode(testPurchaseId);

		String purchasesListUri = URIUtil.format(testResourceServerName, testScope);
		ResourceLink purchasesListLink = ElementListFactory.createListWithoutElement(purchasesListUri, CollectionsMediaTypes.LINKS.id());

		assertResourceState(purchase)
				.self(SelfFactory.createSelf(expectedSelfUri))
				.resourceInfoMaxAge(PurchaseResourceConstants.MAX_AGE)
				.linkCount(1)
				.containsLink(purchasesListLink);

		assertEquals(Base32Util.encode(testPurchaseId), purchase.getEntity().getPurchaseId());
		assertEquals(Base32Util.encode(testOrderId), purchase.getEntity().getOrderId());
		assertEquals(testScope, purchase.getScope());
		assertEquals(testStatus.name(), purchase.getEntity().getStatus());
	}
}
