/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CouponsUriBuilder;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

@RunWith(MockitoJUnitRunner.class)
public final class LinkCouponInfoToOrderStrategyTest {

	private static final String ORDER_URI = "/mockorderuri";
	private static final String ORDER_ID = "orderid";
	private static final String SCOPE = "scope";
	private static final String COUPON_INFO_URI = "/mockcouponinfouri";

	@Mock
	private CouponsUriBuilderFactory couponsUriBuilderFactory;
	@Mock
	private CouponsUriBuilder couponsUriBuilder;

	@InjectMocks
	private LinkCouponInfoToOrderStrategy addCouponInfoLinkToOrderStrategy;

	@Test
	public void testLinkToCouponInfoIsSuccessfullyCreatedForOrder() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();
		when(couponsUriBuilderFactory.get()).thenReturn(couponsUriBuilder);
		when(couponsUriBuilder.setSourceUri(any(String.class))).thenReturn(couponsUriBuilder);
		when(couponsUriBuilder.setInfoUri()).thenReturn(couponsUriBuilder);
		when(couponsUriBuilder.build()).thenReturn(COUPON_INFO_URI);

		Iterable<ResourceLink> createdLinks = addCouponInfoLinkToOrderStrategy.getLinks(orderRepresentation);

		assertThat("The created links should be the same as expected", createdLinks, Matchers.hasItems(createExpectedCouponInfoLink()));
	}

	private ResourceState<OrderEntity> createOrderRepresentation() {
		Self self = SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id());
		OrderEntity orderEntity = OrderEntity.builder()
				.withOrderId(ORDER_ID)
				.build();
		return ResourceState.Builder.create(orderEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}

	private ResourceLink createExpectedCouponInfoLink() {
		return ResourceLinkFactory.create(COUPON_INFO_URI, ControlsMediaTypes.INFO.id(),
				CouponsResourceRels.COUPONINFO_REL,
				CouponsResourceRels.ORDER_REV);
	}
}
