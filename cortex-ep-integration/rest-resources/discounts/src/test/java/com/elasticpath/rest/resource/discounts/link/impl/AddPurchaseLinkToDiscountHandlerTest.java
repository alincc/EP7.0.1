/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.link.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Tests the {@link AddPurchaseLinkToDiscountHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddPurchaseLinkToDiscountHandlerTest {
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "scope";
	private static final String PURCHASE_URI = "/purchaseUri";

	@Mock
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;
	@InjectMocks
	private AddPurchaseLinkToDiscountHandler linkHandler;

	private ResourceState<DiscountEntity> discount;

	@Before
	public void setupCommonTestComponents() {
		PurchaseUriBuilder purchaseUriBuilder = mock(PurchaseUriBuilder.class);
		given(purchaseUriBuilderFactory.get()).willReturn(purchaseUriBuilder);
		given(purchaseUriBuilder.setPurchaseId(PURCHASE_ID)).willReturn(purchaseUriBuilder);
		given(purchaseUriBuilder.setScope(SCOPE)).willReturn(purchaseUriBuilder);
		given(purchaseUriBuilder.build()).willReturn(PURCHASE_URI);

		discount = ResourceState.Builder.create(DiscountEntity.builder()
																.withPurchaseId(PURCHASE_ID)
																.build())
										.withScope(SCOPE)
										.build();
	}

	@Test
	public void ensurePurchaseLinkIsReturnedForPurchaseDiscountAssociation() {
		ResourceLink expectedPurchaseLink = ResourceLinkFactory.create(PURCHASE_URI, PurchasesMediaTypes.PURCHASE.id(),
																DiscountsResourceRels.PURCHASE_REL, DiscountsResourceRels.DISCOUNT_REV);

		assertThat(linkHandler.getLinks(discount), hasItems(expectedPurchaseLink));
	}

	@Test
	public void ensureNoLinksAreReturnedForDiscountWithoutPurchaseAssociation() {
		discount = ResourceState.Builder.create(DiscountEntity.builder()
																.build())
										.build();

		assertTrue("There should be no links returned when discount is not associated with purchase",
				Iterables.isEmpty(linkHandler.getLinks(discount)));
	}
}
