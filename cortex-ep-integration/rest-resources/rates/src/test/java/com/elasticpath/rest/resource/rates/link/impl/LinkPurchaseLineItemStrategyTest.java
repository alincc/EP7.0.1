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

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.definition.rates.RatesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.rates.integration.PurchaseLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link com.elasticpath.rest.resource.rates.link.impl.LinkPurchaseLineItemStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkPurchaseLineItemStrategyTest {

	private static final String PURCHASE_RESOURCE = "purchases";
	private static final String RATES_RESOURCE = "rates";
	private static final String LINEITEMS_RESOURCE = "lineitems";
	private static final String SCOPE = "scope";
	private static final String PURCHASE_ID = "purchase_id";
	private static final String LINEITEM_ID = "lineitem_id";

	@Mock
	private PurchaseLineItemRateLookupStrategy purchaseLineItemRateLookup;

	private LinkPurchaseLineItemStrategy linkPurchaseLineItemStrategy;

	@Before
	public void setUp() {
		linkPurchaseLineItemStrategy = new LinkPurchaseLineItemStrategy(RATES_RESOURCE, purchaseLineItemRateLookup);
	}


	@Test
	public void testRateLinkCreationOnCartLineItem() {
		Self lineItemSelf = createLineItemSelf();
		Self rateSelf = createRateSelf(lineItemSelf);
		ResourceState<PurchaseLineItemEntity> lineItemRepresentation = createPurchaseLineItemRepresentation(lineItemSelf);
		RateEntity rateEntity = RateEntity.builder().build();
		shouldGetExecutionResultFromRateLookup(ExecutionResultFactory.createReadOK(rateEntity));

		Collection<ResourceLink> result = linkPurchaseLineItemStrategy.getLinks(lineItemRepresentation);

		ResourceLink expectedLink = createRateLink(rateSelf);
		assertThat("The strategy should have build a link from the representation.", result, Matchers.hasSize(1));
		assertThat("The expected link should be contained within the collection of links.", result, Matchers.hasItem(expectedLink));
	}


	@Test
	public void testNoRateLinkCreatedForCartLineItemWhenNoRateFound() {
		Self lineItemSelf = createLineItemSelf();
		ResourceState<PurchaseLineItemEntity> lineItemRepresentation = createPurchaseLineItemRepresentation(lineItemSelf);
		shouldGetExecutionResultFromRateLookup(ExecutionResultFactory.<RateEntity>createNotFound());

		Collection<ResourceLink> result = linkPurchaseLineItemStrategy.getLinks(lineItemRepresentation);

		assertThat("The strategy should not build a link from the representation.", result, Matchers.empty());
	}

	private void shouldGetExecutionResultFromRateLookup(final ExecutionResult<RateEntity> result) {
		when(purchaseLineItemRateLookup.getLineItemRate(SCOPE, PURCHASE_ID, LINEITEM_ID))
		.thenReturn(result);
	}

	private Self createLineItemSelf() {
		return SelfFactory.createSelf(
				URIUtil.format(PURCHASE_RESOURCE, SCOPE, LINEITEMS_RESOURCE, SCOPE, LINEITEM_ID),
				PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());
	}

	private Self createRateSelf(final Self lineItemSelf) {
		return SelfFactory.createSelf(
				URIUtil.format(RATES_RESOURCE, lineItemSelf.getUri()),
				RatesMediaTypes.RATE.id());
	}

	private ResourceState<PurchaseLineItemEntity> createPurchaseLineItemRepresentation(final Self lineItemSelf) {
		return ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
						.withPurchaseId(Base32Util.encode(PURCHASE_ID))
						.withLineItemId(Base32Util.encode(LINEITEM_ID))
						.build())
				.withSelf(lineItemSelf)
				.withScope(SCOPE)
				.build();
	}


	private ResourceLink createRateLink(final Self rateSelf) {
		return ResourceLinkFactory.create(rateSelf.getUri(),
				RatesMediaTypes.RATE.id(),
				RateRepresentationRels.RATE_REL,
				RateRepresentationRels.LINE_ITEM_REV);
	}
}
