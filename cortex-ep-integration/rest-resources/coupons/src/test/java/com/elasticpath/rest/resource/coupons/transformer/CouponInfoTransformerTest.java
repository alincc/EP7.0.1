/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.transformer;

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.coupons.impl.CouponsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Test class for {@link CouponInfoTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CouponInfoTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "coupons";
	private static final String SOURCE_URI = "/other/scope/id";
	private static final String SCOPE = "scope";

	@Mock
	private ResourceState<OrderEntity> orderResourceState;

	@Mock
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@InjectMocks
	private CouponInfoTransformer couponInfoTransformer;

	private final Collection<String> couponIds = new ArrayList<>(); // /default empty

	@Before
	public void setUp() {
		when(couponsUriBuilderFactory.get()).thenAnswer(invocation -> new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME));

		Self otherSelf = mock(Self.class);
		when(otherSelf.getUri()).thenReturn(SOURCE_URI);
		when(otherSelf.getType()).thenReturn(ORDER.id());
		when(orderResourceState.getSelf()).thenReturn(otherSelf);
		when(orderResourceState.getScope()).thenReturn(SCOPE);

		OrderEntity orderEntity = OrderEntity.builder()
				.withOrderId("orderId")
				.build();
		when(orderResourceState.getEntity()).thenReturn(orderEntity);
	}

	@Test
	public void testSelfIsCorrectWhenTransformingCouponIdsToInfoRepresentation() {
		Self expectedSelf = buildExpectedSelf();

		ResourceState<InfoEntity> linksResourceState = couponInfoTransformer.transform(couponIds, orderResourceState);

		assertEquals("Expected self does not match.", expectedSelf, linksResourceState.getSelf());
	}

	private Self buildExpectedSelf() {
		String expectedSelfUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
			.setSourceUri(SOURCE_URI)
			.setInfoUri()
			.build();
		return SelfFactory.createSelf(expectedSelfUri);
	}
}
