/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.coupons.impl.CouponsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Test class for {@link CouponFormTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CouponFormTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "coupons";
	private static final String SOURCE_URI = "/otherResource/scope/id";

	@Mock
	private ResourceState<ResourceEntity> otherRepresentation;

	@Mock
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@InjectMocks
	private CouponFormTransformer couponFormTransformer;

	private final CouponEntity entityToTransform = CouponEntity.builder()
			.withCode(StringUtils.EMPTY)
			.build();

	private final Self mockOtherSelf = SelfFactory.createSelf(SOURCE_URI, OrdersMediaTypes.ORDER.id());


	@Before
	public void setUp() {
		when(couponsUriBuilderFactory.get()).thenAnswer(invocation -> new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME));

		when(otherRepresentation.getSelf()).thenReturn(mockOtherSelf);
	}


	@Test
	public void testSelfIsCorrectWhenTransformingCouponEntityToRepresentation() {
		Self expectedSelf = buildExpectedSelf();

		ResourceState<CouponEntity> couponResourceState =
				couponFormTransformer.transform(entityToTransform, otherRepresentation);

		assertEquals("Expected self does not match.", expectedSelf, couponResourceState.getSelf());
	}


	@Test
	public void testRepresentationIsCorrectWhenTransformingCouponEntityToRepresentation() {
		ResourceState<CouponEntity> couponResourceState =
				couponFormTransformer.transform(entityToTransform, otherRepresentation);

		assertEquals("Coupon Code should be correct.", StringUtils.EMPTY, couponResourceState.getEntity().getCode());
	}

	private Self buildExpectedSelf() {
		String expectedSelfUri = new CouponsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(SOURCE_URI)
				.setFormUri()
				.build();
		return SelfFactory.createSelf(expectedSelfUri);
	}
}
