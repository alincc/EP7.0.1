/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Unit test for discount link creation.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkDiscountsToPurchaseStrategyTest extends AbstractLinkDiscountsContractTest<PurchaseEntity> {

	private static final String PURCHASE_ID = "123=";
	@Mock
	PurchaseEntity purchaseEntity;

	@Override
	ResourceState<PurchaseEntity> createLinkingRepresentationUnderTest() {

		return buildResourceStateForPurchase(PURCHASE_ID);
	}

	@Override
	ResourceStateLinkHandler<PurchaseEntity> createLinkStrategyUnderTest() {

		return new LinkDiscountsToPurchaseStrategy(discountsLinkCreator);
	}

	@Test
	public void testNoLinksCreatedForPurchaseFormUsingAbsenceOfPurchaseId() {

		ResourceState<PurchaseEntity> purchaseFormRepresentation = buildResourceStateForPurchase(null);

		Iterable<ResourceLink> links = linkStrategy.getLinks(purchaseFormRepresentation);

		assertEquals("Result should only contain No link.", 0, Iterables.size(links));
	}

	private ResourceState<PurchaseEntity> buildResourceStateForPurchase(final String purchaseId) {

		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withPurchaseId(purchaseId)
				.build();
		return ResourceState.Builder
				.create(purchaseEntity)
				.withSelf(SelfFactory.createSelf(SOURCE_URI, PurchasesMediaTypes.PURCHASE.id()))
				.build();
	}

}
