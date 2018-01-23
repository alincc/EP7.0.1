/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.coupons.impl.CouponsUriBuilderImpl;
import com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test for {@link LinkCouponLinksToPurchaseStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LinkCouponLinksToPurchaseStrategyTest {

	private static final String OTHER_REP_URI = "/mock/other/representation/uri";
	private static final String RESOURCE_SERVER_NAME = "coupons";
	private static final String COUPONS_URI = URIUtil.format(RESOURCE_SERVER_NAME, OTHER_REP_URI);

	@Mock
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@InjectMocks
	private LinkCouponLinksToPurchaseStrategy addCouponLinksToPurchaseStrategy;

	@Test
	public void testLinkToCouponInfoIsSuccessfullyCreatedForOrder() {
		ResourceState<PurchaseEntity> purchaseRepresentaion = createOtherRepresentation();
		when(couponsUriBuilderFactory.get()).thenAnswer(invocation -> new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME));

		Iterable<ResourceLink> createdLinks = addCouponLinksToPurchaseStrategy.getLinks(purchaseRepresentaion);

		assertThat("The created links should be the same as expected", createdLinks, Matchers.hasItems(createExpectedCouponsLink()));
	}

	private ResourceState<PurchaseEntity> createOtherRepresentation() {
		Self self = mock(Self.class);
		when(self.getUri()).thenReturn(OTHER_REP_URI);
		return ResourceState.Builder.create(ResourceTypeFactory.createResourceEntity(PurchaseEntity.class))
				.withSelf(self)
				.build();
	}

	private ResourceLink createExpectedCouponsLink() {
		return ResourceLinkFactory.createNoRev(COUPONS_URI, CollectionsMediaTypes.LINKS.id(),
				CouponsResourceRels.COUPONS_REL);
	}
}
