/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.carts.CartEntity;


/**
 * The test for {@link ShoppingCartTransformer}.
 */
public class ShoppingCartTransformerTest {

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	private final ShoppingCartTransformer transformer = new ShoppingCartTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Test that {@link ShoppingCartTransformer#transformToEntity(Object)} transforms the domain object to an entity.
	 */
	@Test
	public void testTransformToEntity() {
		final String cartGuid = "cartGuid";
		final int totalQuantity = 8;

		CartEntity expectedCartEntity = CartEntity.builder()
				.withCartId(cartGuid)
				.withTotalQuantity(totalQuantity)
				.build();

		final ShoppingCart cartToTransform = context.mock(ShoppingCart.class);
		context.checking(new Expectations() {
			{
				allowing(cartToTransform).getGuid();
				will(returnValue(cartGuid));

				allowing(cartToTransform).getNumItems();
				will(returnValue(totalQuantity));
			}
		});

		CartEntity cartEntity = transformer.transformToEntity(cartToTransform);
		assertEquals(expectedCartEntity, cartEntity);
	}
}
