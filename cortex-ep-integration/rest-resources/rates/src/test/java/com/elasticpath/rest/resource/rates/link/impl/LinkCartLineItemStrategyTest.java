/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.definition.rates.RatesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.rates.integration.CartLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link com.elasticpath.rest.resource.rates.link.impl.LinkCartLineItemStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LinkCartLineItemStrategyTest {

	private static final String CARTS_RESOURCE = "carts";
	private static final String RATES_RESOURCE = "rates";
	private static final String LINEITEMS_RESOURCE = "lineitems";
	private static final String SCOPE = "scope";
	private static final String CART_ID = "cart_id";
	private static final String LINEITEM_ID = "lineitem_id";

	@Mock
	private CartLineItemRateLookupStrategy cartLineItemRateLookup;

	private LinkCartLineItemStrategy linkCartLineItemStrategy;

	@Before
	public void setUp() {
		linkCartLineItemStrategy = new LinkCartLineItemStrategy(RATES_RESOURCE, cartLineItemRateLookup);
	}


	@Test
	public void testRateLinkCreationOnCartLineItem() {
		Self lineItemSelf = SelfFactory.createSelf(
				URIUtil.format(CARTS_RESOURCE, SCOPE, LINEITEMS_RESOURCE, SCOPE, LINEITEM_ID),
				PricesMediaTypes.CART_LINE_ITEM_PRICE.id());
		Self rateSelf = SelfFactory.createSelf(URIUtil.format(RATES_RESOURCE, lineItemSelf.getUri()), RatesMediaTypes.RATE.id());
		RateEntity rateEntity = RateEntity.builder().build();
		when(cartLineItemRateLookup.getLineItemRate(SCOPE, CART_ID, LINEITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(rateEntity));
		ResourceState<LineItemEntity> lineItemRepresentation = createPurchaseLineItemRepresentation(lineItemSelf);
		ResourceLink expectedLink = ResourceLinkFactory.create(rateSelf.getUri(),
				RatesMediaTypes.RATE.id(),
				RateRepresentationRels.RATE_REL,
				RateRepresentationRels.LINE_ITEM_REV);

		Collection<ResourceLink> result = linkCartLineItemStrategy.getLinks(lineItemRepresentation);

		assertThat("The strategy should have build a link from the representation.", result, Matchers.hasSize(1));
		assertThat("The expected link should be contained within the collection of links.", result, Matchers.hasItem(expectedLink));
	}


	@Test
	public void testNoRateLinkCreatedForCartLineItemWhenNoRateFound() {
		Self lineItemSelf = SelfFactory.createSelf(
				URIUtil.format(CARTS_RESOURCE, SCOPE, LINEITEMS_RESOURCE, SCOPE, LINEITEM_ID),
				PricesMediaTypes.CART_LINE_ITEM_PRICE.id());
		when(cartLineItemRateLookup.getLineItemRate(SCOPE, CART_ID, LINEITEM_ID))
				.thenReturn(ExecutionResultFactory.<RateEntity>createNotFound());
		ResourceState<LineItemEntity> lineItemRepresentation = createPurchaseLineItemRepresentation(lineItemSelf);

		Collection<ResourceLink> result = linkCartLineItemStrategy.getLinks(lineItemRepresentation);

		assertThat("The strategy should not build a link from the representation.", result, Matchers.empty());
	}

	private ResourceState<LineItemEntity> createPurchaseLineItemRepresentation(final Self lineItemSelf) {
		return ResourceState.Builder
				.create(LineItemEntity.builder()
						.withCartId(Base32Util.encode(CART_ID))
						.withLineItemId(Base32Util.encode(LINEITEM_ID))
						.build())
				.withSelf(lineItemSelf)
				.withScope(SCOPE)
				.build();
	}
}
