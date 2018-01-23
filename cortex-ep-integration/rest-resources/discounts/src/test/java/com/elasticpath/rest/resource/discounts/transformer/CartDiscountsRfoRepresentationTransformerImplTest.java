/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilder;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Collaboration unit test for the transformer.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartDiscountsRfoRepresentationTransformerImplTest {

	private static final String CART_URI = "/carts/scope/id";
	private static final String DISCOUNT_URI = "/discountUri";
	private static final String CART_ID = "cartId";
	private static final String SCOPE = "scope";
	private CartDiscountsRfoResourceStateTransformerImpl discountsCartTransformer;

	@Mock
	private DiscountsUriBuilderFactory discountsUriBuilderFactory;

	private ResourceState<CartEntity> cartRepresentation;

	@Before
	public void setUp() {
		DiscountsUriBuilder discountsUriBuilder = mock(DiscountsUriBuilder.class);
		discountsCartTransformer = new CartDiscountsRfoResourceStateTransformerImpl(discountsUriBuilderFactory);
		when(discountsUriBuilderFactory.get()).thenReturn(discountsUriBuilder);
		when(discountsUriBuilder.setSourceUri(CART_URI)).thenReturn(discountsUriBuilder);
		when(discountsUriBuilder.build()).thenReturn(DISCOUNT_URI);

		cartRepresentation = ResourceState.Builder.create(CartEntity.builder()
																			.withCartId(CART_ID)
																			.build())
												.withSelf(SelfFactory.createSelf(CART_URI, CartsMediaTypes.CART.id()))
												.withScope(SCOPE)
												.build();
	}

	@Test
	public void testTransform() {
		CostEntity costEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);
		DiscountEntity discountEntity
				= DiscountEntity.builder()
									.addingDiscount(costEntity)
									.build();

		ResourceState<DiscountEntity> expectedDiscount = ResourceState.Builder.create(DiscountEntity.builderFrom(discountEntity)
																										.withCartId(CART_ID)
																										.build())
																			.withScope(SCOPE)
																			.withSelf(SelfFactory.createSelf(DISCOUNT_URI))
																			.build();

		ResourceState<DiscountEntity> transformedDiscount = discountsCartTransformer.transform(discountEntity,
				cartRepresentation);
		assertEquals("The transformed discount should be the same as expected", expectedDiscount, transformedDiscount);
	}
}
